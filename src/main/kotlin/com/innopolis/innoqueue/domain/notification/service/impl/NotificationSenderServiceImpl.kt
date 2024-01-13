package com.innopolis.innoqueue.domain.notification.service.impl

import com.innopolis.innoqueue.domain.notification.dto.NotificationMessageDto
import com.innopolis.innoqueue.domain.notification.enums.NotificationType
import com.innopolis.innoqueue.domain.notification.sender.NotificationSender
import com.innopolis.innoqueue.domain.notification.service.NotificationSenderService
import org.springframework.stereotype.Service

@Service
class NotificationSenderServiceImpl(
    private val notificationSenders: List<NotificationSender>
) : NotificationSenderService {
    override fun sendNotificationMessage(
        notificationType: NotificationType,
        notificationMessageDto: NotificationMessageDto
    ) {
        notificationSenders
            .first { it.notificationType() == notificationType }
            .sendNotificationMessage(notificationMessageDto)
    }
}
