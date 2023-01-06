package com.innopolis.innoqueue.domain.queue.dto

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * DTO for creating new queue
 */
data class NewQueueDto(
    @JsonProperty("name")
    val name: String,
    @JsonProperty("color")
    val color: String,
    @JsonProperty("track_expenses")
    val trackExpenses: Boolean,
)
