package com.innopolis.innoqueue.domain.queue.dto

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * DTO for returning to-do entity
 */
data class ToDoTaskDto(
    @JsonProperty("queue_id")
    val queueId: Long?,

    @JsonProperty("name")
    val name: String?,

    @JsonProperty("color")
    val color: String?,

    @JsonProperty("is_important")
    val isImportant: Boolean?,

    @JsonProperty("track_expenses")
    val trackExpenses: Boolean?,

    @JsonProperty("hash_code")
    val hashCode: Int
)
