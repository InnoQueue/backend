package com.innopolis.innoqueue.domain.user.dto

/**
 * DTO for returning user settings
 */
data class UserDto(
    val userName: String,
    val completed: Boolean,
    val skipped: Boolean,
    val joinedQueue: Boolean,
    val freeze: Boolean,
    val leftQueue: Boolean,
    val yourTurn: Boolean
)
