package com.innopolis.innoqueue.domain.queue.dto

/**
 * DTO for returning queue details
 */
data class QueueDto(
    val queueId: Long,
    val queueName: String,
    val queueColor: String,
    val currentUser: UserExpensesDto,
    val isYourTurn: Boolean,
    val participants: List<UserExpensesDto>,
    val trackExpenses: Boolean,
    val isActive: Boolean,
    val isAdmin: Boolean
)
