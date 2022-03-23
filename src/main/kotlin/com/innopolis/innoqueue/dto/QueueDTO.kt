package com.innopolis.innoqueue.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class QueueDTO(
    @JsonProperty("id")
    val queueId: Long,
    @JsonProperty("name")
    val queueName: String,
    @JsonProperty("color")
    val queueColor: String,
    @JsonProperty("current_user")
    val currentUser: UserExpensesDTO,
    @JsonProperty("participants")
    val participants: List<UserExpensesDTO>,
    @JsonProperty("track_expenses")
    val trackExpenses: Boolean,
    @JsonProperty("is_active")
    val isActive: Boolean,
    @JsonProperty("is_admin")
    val isAdmin: Boolean,
)
