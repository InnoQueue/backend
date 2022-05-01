package com.innopolis.innoqueue.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class EditQueueDTO(
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
