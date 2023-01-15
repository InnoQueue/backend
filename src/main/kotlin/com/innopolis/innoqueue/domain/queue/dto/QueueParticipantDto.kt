package com.innopolis.innoqueue.domain.queue.dto

/**
 * DTO for returning queue details about expenses and it's activity
 */
data class QueueParticipantDto(
    val userId: Long,
    val userName: String,
    val expenses: Long?,
    val active: Boolean,
    val onDuty: Boolean
)
