package com.innopolis.innoqueue.domain.queue.dto

/**
 * DTO for creating new queue
 */
data class NewQueueDto(
    val name: String,
    val color: String,
    val trackExpenses: Boolean,
)
