package com.innopolis.innoqueue.services

import com.innopolis.innoqueue.dao.NotificationRepository
import com.innopolis.innoqueue.dao.QueueRepository
import com.innopolis.innoqueue.dto.NotificationDTO
import com.innopolis.innoqueue.dto.NotificationsListDTO
import com.innopolis.innoqueue.enums.NotificationsType
import com.innopolis.innoqueue.models.Notification
import com.innopolis.innoqueue.models.Queue
import com.innopolis.innoqueue.models.User
import com.innopolis.innoqueue.models.UserQueue
import com.innopolis.innoqueue.rest.v1.dto.EmptyDTO
import com.innopolis.innoqueue.rest.v1.dto.NewNotificationDTO
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.time.ZoneOffset

private const val DELETED_USER_NAME = "Deleted user"
private const val DELETED_QUEUE_NAME = "Deleted queue"
private const val CLEAR_RESPONSE = "Old notifications were deleted"

@Suppress("TooManyFunctions")
@Service
class NotificationsService(
    private val firebaseMessagingService: FirebaseMessagingNotificationsService,
    private val userService: UserService,
    private val queueRepository: QueueRepository,
    private val notificationRepository: NotificationRepository
) {

    fun getNotifications(token: String): NotificationsListDTO {
        val (allNotifications, unreadNotifications) = notificationRepository.findAllByToken(token)
            .partition { it.isRead!! }
        unreadNotifications.readNotifications()
        return NotificationsListDTO(
            unreadNotifications = unreadNotifications.toNotificationDTO(),
            allNotifications = allNotifications.toNotificationDTO()
        )
    }

    fun anyNewNotification(token: String): NewNotificationDTO =
        NewNotificationDTO(notificationRepository.anyUnreadNotification(token))

    fun clearOldNotifications(): EmptyDTO {
        notificationRepository.deleteAll(notificationRepository.findAllExpiredNotifications())
        return EmptyDTO(CLEAR_RESPONSE)
    }

    fun sendNotificationMessage(
        notificationType: NotificationsType,
        participant: User,
        queue: Queue
    ) {
        val notifications = prepareNotificationsListToSend(notificationType, participant, queue)
        notificationRepository.saveAll(notifications)
        firebaseMessagingService.sendNotificationsToFirebase(
            addressees = notifications.mapNotNull { it.user }.map { it.id!! to it.fcmToken },
            notificationType = notificationType,
            participant = participant.id!! to participant.name!!,
            queue = queue.id!! to queue.name!!,
        )
    }

    private fun List<Notification>.readNotifications() {
        for (notification in this) {
            notification.isRead = true
        }
        notificationRepository.saveAll(this)
    }

    private fun List<Notification>.toNotificationDTO() = this.map {
        NotificationDTO(
            messageType = it.messageType!!,
            participantId = it.participantId!!,
            participantName = userService.findUserNameById(it.participantId!!) ?: DELETED_USER_NAME,
            queueId = it.queueId!!,
            queueName = queueRepository.findByIdOrNull(it.queueId!!)?.name ?: DELETED_QUEUE_NAME,
            date = it.date!!
        )
    }

    private fun prepareNotificationsListToSend(
        notificationType: NotificationsType,
        participant: User,
        queue: Queue
    ): List<Notification> = when (notificationType) {
        NotificationsType.SHOOK -> {
            listOf(
                createNotification(
                    recipientUser = participant,
                    participantUserId = participant.id!!,
                    notificationsType = notificationType,
                    referredQueueId = queue.id!!
                )
            )
        }

        else -> {
            queue.userQueues
                .filter { it.shouldSendMessage(notificationType, participant.id!!) }
                .map {
                    createNotification(
                        recipientUser = it.user!!,
                        participantUserId = participant.id!!,
                        notificationsType = notificationType,
                        referredQueueId = queue.id!!
                    )
                }
        }
    }

    private fun createNotification(
        recipientUser: User,
        participantUserId: Long,
        notificationsType: NotificationsType,
        referredQueueId: Long
    ): Notification = Notification().apply {
        user = recipientUser
        participantId = participantUserId
        messageType = notificationsType
        queueId = referredQueueId
        isRead = false
        date = LocalDateTime.now(ZoneOffset.UTC)
    }

    private fun UserQueue.shouldSendMessage(
        notificationsType: NotificationsType,
        participantId: Long
    ): Boolean = if (notificationsType.isRequiredNotification()) {
        true
    } else {
        this.user!!.isUserSubscribed(notificationsType, participantId)
    }

    private fun NotificationsType.isRequiredNotification(): Boolean =
        when (this) {
            NotificationsType.SHOOK, NotificationsType.DELETE_QUEUE -> true
            else -> false
        }

    private fun User.isUserSubscribed(notificationType: NotificationsType, participantId: Long): Boolean =
        if (this.id == participantId) {
            true
        } else {
            val userSetting = this.settings!!
            when (notificationType) {
                NotificationsType.COMPLETED -> userSetting.completed!!
                NotificationsType.SKIPPED -> userSetting.skipped!!
                NotificationsType.JOINED_QUEUE -> userSetting.joinedQueue!!
                NotificationsType.FROZEN, NotificationsType.UNFROZEN -> userSetting.freeze!!
                NotificationsType.LEFT_QUEUE -> userSetting.leftQueue!!
                NotificationsType.YOUR_TURN -> userSetting.yourTurn!!
                else -> true
            }
        }
}
