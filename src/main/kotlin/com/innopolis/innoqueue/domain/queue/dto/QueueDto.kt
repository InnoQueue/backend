package com.innopolis.innoqueue.domain.queue.dto

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * DTO for returning queue details
 */
data class QueueDto(
    @JsonProperty("id")
    val queueId: Long,
    @JsonProperty("name")
    val queueName: String,
    @JsonProperty("color")
    val queueColor: String,
    @JsonProperty("on_duty")
    val currentUser: UserExpensesDto,
    @JsonProperty("is_on_duty")
    val isYourTurn: Boolean,
    @JsonProperty("participants")
    val participants: List<UserExpensesDto>,
    @JsonProperty("track_expenses")
    val trackExpenses: Boolean,
    @JsonProperty("is_active")
    val isActive: Boolean,
    @JsonProperty("is_admin")
    val isAdmin: Boolean,
    @JsonProperty("hash_code")
    var hashCode: Int
)
