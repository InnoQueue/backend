package com.innopolis.innoqueue.domain.queue.dto

/**
 * DTO for returning queue details
 */
data class QueueDetailsDto(
    val queueId: Long,
    val queueName: String,
    val queueColor: String,
//    val currentUser: UserExpensesDto,
//    val yourTurn: Boolean,
    val participants: List<QueueParticipantDto>,
    val trackExpenses: Boolean,
    val active: Boolean,
    val admin: Boolean
)
