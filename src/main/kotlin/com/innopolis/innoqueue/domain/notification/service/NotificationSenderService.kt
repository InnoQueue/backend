package com.innopolis.innoqueue.domain.notification.service

import com.innopolis.innoqueue.domain.notification.dto.NotificationMessageDto
import com.innopolis.innoqueue.domain.notification.enums.NotificationType

interface NotificationSenderService {
    fun sendNotificationMessage(notificationType: NotificationType, notificationMessageDto: NotificationMessageDto)
}
