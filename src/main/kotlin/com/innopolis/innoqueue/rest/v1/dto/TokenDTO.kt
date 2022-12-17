package com.innopolis.innoqueue.rest.v1.dto

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * DTO returns as a response with credentials to a client
 */
data class TokenDTO(
    @JsonProperty("token")
    val token: String,
    @JsonProperty("user_id")
    val userId: Long,
)
