package com.innopolis.innoqueue.dto

import com.fasterxml.jackson.annotation.JsonProperty
import com.innopolis.innoqueue.enums.NotificationsType
import java.time.LocalDateTime

data class NotificationDTO(
    @JsonProperty("message_type")
    val messageType: NotificationsType,
    @JsonProperty("participant_id")
    val participantId: Long,
    @JsonProperty("participant_name")
    val participantName: String?,
    @JsonProperty("queue_id")
    val queueId: Long,
    @JsonProperty("queue_name")
    val queueName: String,
    @JsonProperty("timestamp")
    val date: LocalDateTime,
)
