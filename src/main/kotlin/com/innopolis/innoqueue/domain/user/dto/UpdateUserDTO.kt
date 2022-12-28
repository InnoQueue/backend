package com.innopolis.innoqueue.domain.user.dto

/**
 * DTO for updating user settings
 */
data class UpdateUserDTO(
    val userName: String?,
    val completed: Boolean?,
    val skipped: Boolean?,
    val joinedQueue: Boolean?,
    val freeze: Boolean?,
    val leftQueue: Boolean?,
    val yourTurn: Boolean?
)
