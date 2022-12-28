package com.innopolis.innoqueue.domain.queue.dto

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * DTO for returning list of active and frozen queues
 */
data class QueuesListDTO(
    @JsonProperty("active")
    val activeQueues: List<QueueShortDTO>,

    @JsonProperty("frozen")
    val frozenQueues: List<QueueShortDTO>
)
