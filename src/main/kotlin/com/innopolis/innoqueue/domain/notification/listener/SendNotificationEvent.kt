package com.innopolis.innoqueue.domain.notification.listener

import com.innopolis.innoqueue.domain.notification.dto.NotificationMessageDto
import com.innopolis.innoqueue.domain.notification.enums.NotificationType
import com.innopolis.innoqueue.domain.notification.model.Notification

data class SendNotificationEvent(
    val notificationType: NotificationType,
    val notificationMessageDto: NotificationMessageDto,
    val notifications: List<Notification>
)
