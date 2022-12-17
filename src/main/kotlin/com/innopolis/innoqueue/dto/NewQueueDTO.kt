package com.innopolis.innoqueue.dto

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * DTO for creating new queue
 */
data class NewQueueDTO(
    @JsonProperty("name")
    val name: String,
    @JsonProperty("color")
    val color: String,
    @JsonProperty("track_expenses")
    val trackExpenses: Boolean,
)
