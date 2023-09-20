package com.innopolis.innoqueue.domain.notification.service

import com.innopolis.innoqueue.domain.notification.dto.NotificationDto
import com.innopolis.innoqueue.domain.notification.enums.NotificationType
import com.innopolis.innoqueue.rest.v1.dto.EmptyDto
import com.innopolis.innoqueue.rest.v1.dto.NewNotificationDto
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface NotificationService {
    fun getNotifications(token: String, pageable: Pageable): Page<NotificationDto>

    fun anyNewNotification(token: String): NewNotificationDto

    fun readNotifications(token: String, notificationIds: List<Long>? = null)

    fun clearOldNotifications(): EmptyDto

    fun deleteNotifications(token: String, notificationIds: List<Long>? = null)

    fun deleteNotificationById(token: String, notificationId: Long)

    fun sendNotificationMessage(
        notificationType: NotificationType,
        participantId: Long,
        participantName: String,
        queueId: Long,
        queueName: String
    )
}
