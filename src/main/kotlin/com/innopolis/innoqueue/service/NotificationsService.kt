package com.innopolis.innoqueue.service

import com.innopolis.innoqueue.dto.NotificationDTO
import com.innopolis.innoqueue.dto.NotificationsListDTO
import com.innopolis.innoqueue.model.Notification
import com.innopolis.innoqueue.model.Queue
import com.innopolis.innoqueue.model.User
import com.innopolis.innoqueue.repository.NotificationRepository
import com.innopolis.innoqueue.repository.QueueRepository
import com.innopolis.innoqueue.utils.NotificationsTypes
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.time.ZoneOffset

@Service

class NotificationsService(
    private val userService: UserService,
    private val notificationRepository: NotificationRepository,
    private val queueRepository: QueueRepository,
) {

    private val deletedUserName = "Deleted user"
    private val deletedQueueName = "Deleted queue"

    fun getNotifications(token: String): NotificationsListDTO {
        val user = userService.getUserByToken(token)
        val (allNotifications, unreadNotifications) = user.notifications.partition { it.isRead!! }
        for (notification in unreadNotifications) {
            notification.isRead = true
        }
        notificationRepository.saveAll(unreadNotifications)
        return NotificationsListDTO(
            unreadNotifications = mapEntityToDTO(unreadNotifications).sortedBy { n -> n.date },
            allNotifications = mapEntityToDTO(allNotifications).sortedByDescending { n -> n.date }
        )
    }

    fun createNotificationMessage(
        notificationType: NotificationsTypes,
        participant: User,
        queue: Queue
    ) {
        when (notificationType) {
            NotificationsTypes.SHOOK -> {
                val notification = Notification()
                notification.user = participant
                notification.participantId = participant.id!!
                notification.messageType = this.convertToMessageType(notificationType)
                notification.queueId = queue.id!!
                notification.isRead = false
                notification.date = LocalDateTime.now(ZoneOffset.UTC)
                notificationRepository.save(notification)
            }
            else -> {
                val notifications = mutableListOf<Notification>()
                for (userQueue in queue.userQueues) {
                    val notification = Notification()
                    notification.user = userQueue.user
                    notification.participantId = participant.id!!
                    notification.messageType = this.convertToMessageType(notificationType)
                    notification.queueId = queue.id!!
                    notification.isRead = false
                    notification.date = LocalDateTime.now(ZoneOffset.UTC)
                    notifications.add(notification)
                }
                notificationRepository.saveAll(notifications)
            }
        }
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
