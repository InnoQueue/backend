package com.innopolis.innoqueue.rest.v0.dto

import io.swagger.v3.oas.annotations.media.Schema

/**
 * DTO with notification ids
 */
@Schema(description = "Notification ids list")
data class NotificationIdsDto(
    @Schema(description = "List of notification ids")
    val notificationIds: List<Long>
)
