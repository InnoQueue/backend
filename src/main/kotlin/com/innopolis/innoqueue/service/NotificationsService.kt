package com.innopolis.innoqueue.service

import com.innopolis.innoqueue.controller.dto.EmptyDTO
import com.innopolis.innoqueue.controller.dto.NewNotificationDTO
import com.innopolis.innoqueue.dto.NotificationDTO
import com.innopolis.innoqueue.dto.NotificationsListDTO
import com.innopolis.innoqueue.model.Notification
import com.innopolis.innoqueue.model.Queue
import com.innopolis.innoqueue.model.User
import com.innopolis.innoqueue.model.UserQueue
import com.innopolis.innoqueue.repository.NotificationRepository
import com.innopolis.innoqueue.repository.QueueRepository
import com.innopolis.innoqueue.utils.MessagePushNotificationCreator
import com.innopolis.innoqueue.utils.NotificationsTypes
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.time.ZoneOffset

@Service

class NotificationsService(
    private val userService: UserService,
    private val firebaseMessagingService: FirebaseMessagingNotificationsService,
    private val notificationRepository: NotificationRepository,
    private val queueRepository: QueueRepository,
) {

    private val deletedUserName = "Deleted user"
    private val deletedQueueName = "Deleted queue"
    private val notificationLiveTimeWeeks: Long = 2

    fun getNotifications(token: String): NotificationsListDTO {
        val user = userService.getUserByToken(token)
        val (allNotifications, unreadNotifications) = user.notifications.partition { it.isRead!! }
        for (notification in unreadNotifications) {
            notification.isRead = true
        }
        notificationRepository.saveAll(unreadNotifications)
        return NotificationsListDTO(
            unreadNotifications = mapEntityToDTO(unreadNotifications).sortedByDescending { n -> n.date },
            allNotifications = mapEntityToDTO(allNotifications).sortedByDescending { n -> n.date }
        )
    }

    fun anyNewNotification(token: String): NewNotificationDTO {
        val user = userService.getUserByToken(token)
        return NewNotificationDTO(user.notifications.any { !it.isRead!! })
    }

    fun clearOldNotifications(): EmptyDTO {

        val currentTime = LocalDateTime.now(ZoneOffset.UTC)

        val expiredNotifications =
            notificationRepository.findAll().filter { isNotificationExpired(currentTime, it.date!!) }
        notificationRepository.deleteAll(expiredNotifications)

        return EmptyDTO("Old notifications were deleted")
    }

    private fun isNotificationExpired(currentTime: LocalDateTime, date: LocalDateTime): Boolean {
        val dateExpired = date.plusWeeks(notificationLiveTimeWeeks)
        return currentTime > dateExpired
    }

    fun createNotificationMessage(
        notificationType: NotificationsTypes,
        participant: User,
        queue: Queue
    ) {
        val notifications = prepareNotificationsListToSend(notificationType, participant, queue)
        notificationRepository.saveAll(notifications)
        sendNotificationsToFirebase(notifications, notificationType, participant, queue)
    }

    private fun sendNotificationsToFirebase(
        notifications: List<Notification>,
        notificationType: NotificationsTypes,
        participant: User,
        queue: Queue
    ) {
        for (message in notifications) {
            val isPersonal = message.user?.id!! == participant.id!!
            val queueName = queue.name!!
            val queueId = queue.id!!
            val participantName = participant.name!!
            val (title, body) = MessagePushNotificationCreator(
                notificationType,
                queueName,
                isPersonal,
                participantName
            ).getTitleAndBodyForMessage()
            if (title != null && body != null) {
                val token = message.user?.fcmToken!!
                try {
                    val dataMap = HashMap<String, String?>()
                    dataMap["title"] = title
                    dataMap["body"] = body
                    dataMap["queue_id"] = queueId.toString()
                    dataMap["queue_name"] = queueName
                    dataMap["participant_name"] = participantName
                    val res = firebaseMessagingService.sendNotification(title, body, token, dataMap)
                    println("Firebase result: $res")
                } catch (e: Exception) {
                    println("Firebase exception: $e")
                }
            }
        }
    }

    private fun prepareNotificationsListToSend(
        notificationType: NotificationsTypes,
        participant: User,
        queue: Queue
    ): List<Notification> {
        val messageType = this.convertToMessageType(notificationType)
        val notifications = mutableListOf<Notification>()

        when (notificationType) {
            NotificationsTypes.SHOOK -> {
                val notification = createNotification(participant, participant.id!!, messageType, queue.id!!)
                notifications.add(notification)
            }
            else -> {
                val participantId = participant.id!!
                for (userQueue in queue.userQueues) {
                    if (shouldSendMessage(notificationType, userQueue, participantId)) {
                        val notification =
                            createNotification(userQueue.user!!, participantId, messageType, queue.id!!)
                        notifications.add(notification)
                    }
                }
            }
        }
        return notifications
    }

    private fun shouldSendMessage(
        notificationType: NotificationsTypes,
        userQueue: UserQueue,
        participantId: Long
    ): Boolean {
        return if (isRequiredNotification(notificationType)) {
            true
        } else {
            isUserSubscribed(notificationType, userQueue.user!!, participantId)
        }
    }

    private fun isUserSubscribed(notificationType: NotificationsTypes, user: User, participantId: Long): Boolean {
        return if (user.id == participantId) {
            true
        } else {
            val userSetting = user.settings!!
            when (notificationType) {
                NotificationsTypes.COMPLETED -> userSetting.completed!!
                NotificationsTypes.SKIPPED -> userSetting.skipped!!
                NotificationsTypes.JOINED_QUEUE -> userSetting.joinedQueue!!
                NotificationsTypes.FROZEN, NotificationsTypes.UNFROZEN -> userSetting.freeze!!
                NotificationsTypes.LEFT_QUEUE -> userSetting.leftQueue!!
                NotificationsTypes.YOUR_TURN -> userSetting.yourTurn!!
                else -> true
            }
        }
    }

    private fun isRequiredNotification(notificationType: NotificationsTypes): Boolean {
        return when (notificationType) {
            NotificationsTypes.COMPLETED, NotificationsTypes.SKIPPED,
            NotificationsTypes.JOINED_QUEUE, NotificationsTypes.FROZEN,
            NotificationsTypes.UNFROZEN, NotificationsTypes.LEFT_QUEUE, NotificationsTypes.YOUR_TURN -> false
            else -> true
        }
    }

    private fun createNotification(user: User, participantId: Long, messageType: String, queueId: Long): Notification {
        val notification = Notification()
        notification.user = user
        notification.participantId = participantId
        notification.messageType = messageType
        notification.queueId = queueId
        notification.isRead = false
        notification.date = LocalDateTime.now(ZoneOffset.UTC)
        return notification
    }

    private fun mapEntityToDTO(notifications: List<Notification>): List<NotificationDTO> =
        notifications.map { n ->
            NotificationDTO(
                messageType = n.messageType!!,
                participantId = n.participantId!!,
                participantName = this.getParticipantNameById(n.participantId!!),
                queueId = n.queueId!!,
                queueName = this.getQueueNameById(n.queueId!!),
                date = n.date!!
            )
        }

    private fun getParticipantNameById(participantId: Long): String {
        return userService.getUserById(participantId)?.name ?: deletedUserName
    }

    private fun getQueueNameById(queueId: Long): String {
        return queueRepository.findByIdOrNull(queueId)?.name ?: deletedQueueName
    }

    private fun convertToMessageType(notificationType: NotificationsTypes): String {
        return when (notificationType) {
            NotificationsTypes.YOUR_TURN -> "YOUR_TURN"
            NotificationsTypes.SHOOK -> "SHOOK"
            NotificationsTypes.FROZEN -> "FROZEN"
            NotificationsTypes.UNFROZEN -> "UNFROZEN"
            NotificationsTypes.JOINED_QUEUE -> "JOINED_QUEUE"
            NotificationsTypes.LEFT_QUEUE -> "LEFT_QUEUE"
            NotificationsTypes.DELETE_QUEUE -> "DELETE_QUEUE"
            NotificationsTypes.COMPLETED -> "COMPLETED"
            NotificationsTypes.SKIPPED -> "SKIPPED"
        }
    }
}
