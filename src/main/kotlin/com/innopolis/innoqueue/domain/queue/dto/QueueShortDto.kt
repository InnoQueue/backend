package com.innopolis.innoqueue.domain.queue.dto

/**
 * DTO for returning a short queue description
 */
class QueueShortDto(
    val queueId: Long,
    val queueName: String,
    val queueColor: String,
    val onDutyUser: String,
    val active: Boolean
)
