package com.innopolis.innoqueue.domain.queue.dto

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * DTO for editing an existing queue
 */
data class EditQueueDto(
    @JsonProperty("id")
    val queueId: Long?,
    @JsonProperty("name")
    val name: String?,
    @JsonProperty("color")
    val color: String?,
    @JsonProperty("track_expenses")
    val trackExpenses: Boolean?,
    @JsonProperty("participants")
    val participants: List<Long>?
)
