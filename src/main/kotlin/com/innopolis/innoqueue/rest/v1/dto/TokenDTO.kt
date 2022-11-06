package com.innopolis.innoqueue.rest.v1.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class TokenDTO(
    @JsonProperty("token")
    val token: String,
    @JsonProperty("user_id")
    val userId: Long,
)
