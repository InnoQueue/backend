package com.innopolis.innoqueue.domain.queue.dto

/**
 * DTO for returning to-do entity
 */
data class ToDoTaskDto(
    val queueId: Long?,
    val queueName: String?,
    val queueColor: String?,
    val important: Boolean?,
    val trackExpenses: Boolean?
)
