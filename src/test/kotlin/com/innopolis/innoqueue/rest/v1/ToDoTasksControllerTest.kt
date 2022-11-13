package com.innopolis.innoqueue.rest.v1

import com.innopolis.innoqueue.rest.v1.dto.TaskDTO
import com.innopolis.innoqueue.services.ToDoTaskService
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test

class ToDoTasksControllerTest {

    @Test
    fun `Test getTasks service called`() {
        // given
        val token = "token"
        val service = mockk<ToDoTaskService>(relaxed = true)
        val controller = ToDoTasksController(service)

        // when
        controller.getToDoTasks(token)

        // then
        verify(exactly = 1) { service.getToDoTasks(token) }
    }

    @Test
    fun `Test completeTask service called`() {
        // given
        val token = "token"
        val taskDTO = TaskDTO(
            taskId = 1L,
            expenses = 10.0
        )
        val service = mockk<ToDoTaskService>(relaxed = true)
        val controller = ToDoTasksController(service)

        // when
        controller.completeTask(token, taskDTO)

        // then
        verify(exactly = 1) { service.completeTask(token, taskDTO.taskId, taskDTO.expenses) }
    }

    @Test
    fun `Test skipTask service called`() {
        // given
        val token = "token"
        val taskDTO = TaskDTO(
            taskId = 1L,
            expenses = null
        )
        val service = mockk<ToDoTaskService>(relaxed = true)
        val controller = ToDoTasksController(service)

        // when
        controller.skipTask(token, taskDTO)

        // then
        verify(exactly = 1) { service.skipTask(token, taskDTO.taskId) }
    }
}
