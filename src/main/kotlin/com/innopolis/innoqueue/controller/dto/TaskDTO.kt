package com.innopolis.innoqueue.controller.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class TaskDTO(
    @JsonProperty("task_id")
    val taskId: Long,

    @JsonProperty("expenses")
    val expenses: Int?
)