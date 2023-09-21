package com.innopolis.innoqueue.domain.queue.service

import com.innopolis.innoqueue.domain.queue.dto.ToDoTaskDto

interface ToDoTaskService {
    fun getToDoTasks(token: String): List<ToDoTaskDto>

    fun completeTask(token: String, taskId: Long, expenses: Long?)

    fun skipTask(token: String, taskId: Long)
}
