package com.innopolis.innoqueue.domain.notification.sender

import com.innopolis.innoqueue.domain.notification.dto.NotificationMessageDto
import com.innopolis.innoqueue.domain.notification.enums.NotificationType

interface NotificationSender {

    fun sendNotificationMessage(notificationMessageDto: NotificationMessageDto)

    fun notificationType(): NotificationType
}
