package com.innopolis.innoqueue.service

import com.innopolis.innoqueue.dto.NotificationDTO
import com.innopolis.innoqueue.dto.NotificationsListDTO
import com.innopolis.innoqueue.model.Notification
import com.innopolis.innoqueue.repository.NotificationRepository
import org.springframework.stereotype.Service

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
}
