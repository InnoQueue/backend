package com.innopolis.innoqueue.domain.notification.service

import com.innopolis.innoqueue.domain.notification.dto.NotificationMessageDto

interface NotificationSenderService {

    fun sendNotificationMessage(notificationMessageDto: NotificationMessageDto)
}
