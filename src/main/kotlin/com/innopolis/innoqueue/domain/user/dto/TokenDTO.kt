package com.innopolis.innoqueue.domain.user.dto

/**
 * DTO returns as a response with credentials to a client
 */
data class TokenDTO(
    val token: String,
    val userId: Long,
)
