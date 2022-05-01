package com.innopolis.innoqueue.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class QueueDTO(
    @JsonProperty("id")
    val queueId: Long,
    @JsonProperty("name")
    val queueName: String,
    @JsonProperty("color")
    val queueColor: String,
    @JsonProperty("on_duty")
    val currentUser: UserExpensesDTO,
    @JsonProperty("is_on_duty")
    val isYourTurn: Boolean,
    @JsonProperty("participants")
    val participants: List<UserExpensesDTO>,
    @JsonProperty("track_expenses")
    val trackExpenses: Boolean,
    @JsonProperty("is_active")
    val isActive: Boolean,
    @JsonProperty("is_admin")
    val isAdmin: Boolean,
    @JsonProperty("hash_code")
    var hashCode: Int
)
