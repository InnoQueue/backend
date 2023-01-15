package com.innopolis.innoqueue.domain.queue.dto

/**
 * DTO for returning list of active and frozen queues
 */
data class QueuesListDto(
    val queues: List<QueueShortDto>
)
