package com.innopolis.innoqueue.service

import com.innopolis.innoqueue.dto.NotificationDTO
import com.innopolis.innoqueue.dto.NotificationsListDTO
import com.innopolis.innoqueue.model.UserNotification
import com.innopolis.innoqueue.repository.UserNotificationsRepository
import org.springframework.stereotype.Service

@Service

class NotificationsService(
    private val userService: UserService,
    private val notificationsRepository: UserNotificationsRepository,
) {
    fun getNotifications(token: String): NotificationsListDTO {
        val user = userService.getUserByToken(token)
        val (allNotifications, unreadNotifications) = user.notifications.partition { it.isRead!! }
        for (notification in unreadNotifications) {
            //todo uncomment
            //notification.isRead = true
        }
        notificationsRepository.saveAll(unreadNotifications)
        return NotificationsListDTO(
            unreadNotifications = mapEntityToDTO(unreadNotifications).sortedBy { n -> n.date },
            allNotifications = mapEntityToDTO(allNotifications).sortedByDescending { n -> n.date }
        )
    }

    private fun mapEntityToDTO(notifications: List<UserNotification>): List<NotificationDTO> =
        notifications.map { n -> NotificationDTO(n.message!!, n.date!!) }
}
