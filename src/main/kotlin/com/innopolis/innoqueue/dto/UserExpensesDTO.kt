package com.innopolis.innoqueue.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class UserExpensesDTO(
    @JsonProperty("user")
    val userName: String,
    @JsonProperty("expenses")
    val expenses: Int?
)