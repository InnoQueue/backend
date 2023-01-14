package com.innopolis.innoqueue.rest.v0.dto

import com.innopolis.innoqueue.domain.queue.dto.ToDoTaskDto

/**
 * DTO
 */
data class ToDoTasksListDto(
    val toDoTasks: List<ToDoTaskDto>
)
