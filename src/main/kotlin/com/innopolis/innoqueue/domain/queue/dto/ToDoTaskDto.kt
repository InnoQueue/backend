package com.innopolis.innoqueue.domain.queue.dto

/**
 * DTO for returning to-do entity
 */
data class ToDoTaskDto(
    val queueId: Long?,
    val name: String?,
    val color: String?,
    val important: Boolean?,
    val trackExpenses: Boolean?
)
