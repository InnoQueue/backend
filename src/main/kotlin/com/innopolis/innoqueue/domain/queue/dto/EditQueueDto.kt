package com.innopolis.innoqueue.domain.queue.dto

/**
 * DTO for editing an existing queue
 */
data class EditQueueDto(
    val queueName: String?,
    val queueColor: String?,
    val trackExpenses: Boolean?,
    val participants: List<Long>?
)
