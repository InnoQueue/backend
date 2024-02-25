package com.innopolis.innoqueue.rest.v1.dto

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * DTO for completing a particular queue
 */
data class TaskDTO(
    @JsonProperty("task_id")
    val taskId: Long,

    @JsonProperty("expenses")
    val expenses: Double?
)
