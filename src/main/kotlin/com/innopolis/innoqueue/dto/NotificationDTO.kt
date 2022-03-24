package com.innopolis.innoqueue.dto

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDateTime

data class NotificationDTO(
    @JsonProperty("message")
    val message: String,
    @JsonProperty("timestamp")
    val date: LocalDateTime,
)