package com.innopolis.innoqueue.rest.v0.dto

import io.swagger.v3.oas.annotations.media.Schema

/**
 * DTO with notification ids which should be marked as read
 */
@Schema(description = "Notification ids which should be marked as read")
data class ReadNotificationDto(
    @Schema(description = "List of notification ids. It can be null, so all notifications will be marked as read.")
    val notificationIds: List<Long>
)
