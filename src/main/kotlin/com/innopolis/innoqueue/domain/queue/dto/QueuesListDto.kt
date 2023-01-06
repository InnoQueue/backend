package com.innopolis.innoqueue.domain.queue.dto

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * DTO for returning list of active and frozen queues
 */
data class QueuesListDto(
    @JsonProperty("active")
    val activeQueues: List<QueueShortDto>,

    @JsonProperty("frozen")
    val frozenQueues: List<QueueShortDto>
)
