package com.innopolis.innoqueue.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class QueuesListDTO(
    @JsonProperty("active")
    val activeQueues: List<QueueDTO>,

    @JsonProperty("frozen")
    val frozenQueues: List<QueueDTO>
)
