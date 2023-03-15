package com.innopolis.innoqueue.domain.notification.dto

import com.innopolis.innoqueue.domain.notification.enums.NotificationType
import java.time.LocalDateTime

/**
 * DTO for returning a notification message
 */
data class NotificationDto(
    val notificationId: Long,
    val messageType: NotificationType,
    val message: String?,
    val participantId: Long?,
    val participantName: String?,
    val queueId: Long?,
    val queueName: String?,
    val date: LocalDateTime,
    val read: Boolean
)
