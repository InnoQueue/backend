package com.innopolis.innoqueue.domain.notification.dto

import com.innopolis.innoqueue.domain.notification.enums.NotificationsType
import java.time.LocalDateTime

/**
 * DTO for returning a notification message
 */
data class NotificationDto(
    val notificationId: Long,
    val messageType: NotificationsType,
    val participantId: Long?,
    val participantName: String?,
    val queueId: Long?,
    val queueName: String,
    val date: LocalDateTime,
)
