package com.innopolis.innoqueue.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class UserExpensesDTO(
    @JsonProperty("user_id")
    val userId: Long,
    @JsonProperty("user")
    val userName: String,
    @JsonProperty("expenses")
    val expenses: Int?,
    @JsonProperty("is_active")
    val isActive: Boolean
)