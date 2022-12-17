package com.innopolis.innoqueue.dto

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * DTO for returning queue details about expenses and it's activity
 */
data class UserExpensesDTO(
    @JsonProperty("user_id")
    val userId: Long,
    @JsonProperty("user")
    val userName: String,
    @JsonProperty("expenses")
    val expenses: Double?,
    @JsonProperty("is_active")
    val isActive: Boolean
)
