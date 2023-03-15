package com.innopolis.innoqueue.domain.queue.dto

/**
 * DTO for creating new queue
 */
data class NewQueueDto(
    val queueName: String,
    val queueColor: String,
    val trackExpenses: Boolean,
)
