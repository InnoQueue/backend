package com.innopolis.innoqueue.rest.v1.dto

import com.innopolis.innoqueue.domain.queue.dto.ToDoTaskDto

/**
 * DTO
 */
data class ToDoTasksListDto(
    val toDoTasks: List<ToDoTaskDto>
)
