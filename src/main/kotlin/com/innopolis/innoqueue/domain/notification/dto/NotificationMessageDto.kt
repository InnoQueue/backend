package com.innopolis.innoqueue.domain.notification.dto

/**
 * DTO for creating a notification message to be sent
 */
data class NotificationMessageDto(
    val participantId: Long,
    val participantName: String,
    val queueId: Long,
    val queueName: String
)
