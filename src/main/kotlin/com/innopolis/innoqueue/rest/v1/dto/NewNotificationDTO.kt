package com.innopolis.innoqueue.rest.v1.dto

import com.fasterxml.jackson.annotation.JsonProperty
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Information about a new notification")
data class NewNotificationDTO(
    @Schema(description = "Whether there are any unread notifications")
    @JsonProperty("any_new")
    val anyNew: Boolean
)
