package com.innopolis.innoqueue.service

import com.innopolis.innoqueue.dto.NotificationDTO
import com.innopolis.innoqueue.dto.NotificationsListDTO
import com.innopolis.innoqueue.model.Notification
import com.innopolis.innoqueue.model.Queue
import com.innopolis.innoqueue.model.User
import com.innopolis.innoqueue.repository.NotificationRepository
import com.innopolis.innoqueue.utils.NotificationsTypes
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service

class NotificationsService(
    private val userService: UserService,
    private val notificationRepository: NotificationRepository,
) {
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
                notification.participant = participant
                notification.messageType = this.convertToMessageType(notificationType)
                notification.queue = queue
                notification.isRead = false
                notification.date = LocalDateTime.now()
                notificationRepository.save(notification)
            }
            else -> {
                val notifications = mutableListOf<Notification>()
                for (userQueue in queue.userQueues) {
                    val notification = Notification()
                    notification.user = userQueue.user
                    notification.participant = participant
                    notification.messageType = this.convertToMessageType(notificationType)
                    notification.queue = queue
                    notification.isRead = false
                    notification.date = LocalDateTime.now()
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
                participantName = n.participant?.name,
                queueId = n.queue?.id!!,
                queueName = n.queue?.name!!,
                date = n.date!!
            )
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
