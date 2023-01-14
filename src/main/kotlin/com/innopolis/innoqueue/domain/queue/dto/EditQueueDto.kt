package com.innopolis.innoqueue.domain.queue.dto

/**
 * DTO for editing an existing queue
 */
data class EditQueueDto(
    val queueId: Long?,
    val name: String?,
    val color: String?,
    val trackExpenses: Boolean?,
    val participants: List<Long>?
)
