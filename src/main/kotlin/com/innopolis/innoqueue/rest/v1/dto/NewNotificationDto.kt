package com.innopolis.innoqueue.rest.v1.dto

import io.swagger.v3.oas.annotations.media.Schema

/**
 * DTO with boolean flag indicating whether there is any unread message
 */
@Schema(description = "Information about a new notification")
data class NewNotificationDto(
    @Schema(description = "Whether there are any unread notifications")
    val anyNew: Boolean
)
