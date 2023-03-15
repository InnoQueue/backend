package com.innopolis.innoqueue.domain.queue.dto

/**
 * DTO for returning queue details
 */
data class QueueDetailsDto(
    val queueId: Long,
    val queueName: String,
    val queueColor: String,
    val trackExpenses: Boolean,
    val admin: Boolean,
    val participants: List<QueueParticipantDto>,
//    val currentUser: UserExpensesDto,
//    val yourTurn: Boolean,
//    val active: Boolean,
)
