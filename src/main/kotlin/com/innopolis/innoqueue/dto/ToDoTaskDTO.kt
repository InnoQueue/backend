package com.innopolis.innoqueue.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class ToDoTaskDTO(
    @JsonProperty("task_id")
    val taskId: Long?,

    @JsonProperty("name")
    val name: String?,

    @JsonProperty("color")
    val color: String?,

    @JsonProperty("is_important")
    val isImportant: Boolean?,

    @JsonProperty("track_expenses")
    val trackExpenses: Boolean?
)