package com.innopolis.innoqueue.rest.v0

import com.innopolis.innoqueue.domain.queue.dto.EditQueueDto
import com.innopolis.innoqueue.domain.queue.dto.NewQueueDto
import com.innopolis.innoqueue.domain.queue.dto.QueueInviteCodeDto
import com.innopolis.innoqueue.domain.queue.service.QueueService
import com.innopolis.innoqueue.domain.queue.service.ToDoTaskService
import com.innopolis.innoqueue.rest.v0.dto.ExpensesDto
import com.innopolis.innoqueue.rest.v0.dto.QueueActivityDto
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test

class QueueControllerTest {

    @Test
    fun `Test getQueues service called`() {
        // given
        val token = "token"
        val service = mockk<QueueService>(relaxed = true)
        val controller = QueueController(service, mockk())

        // when
        controller.getQueues(token, null)

        // then
        verify(exactly = 1) { service.getQueues(token, null) }
    }

    @Test
    fun `Test getQueueById service called`() {
        // given
        val token = "token"
        val queueId = 1L
        val service = mockk<QueueService>(relaxed = true)
        val controller = QueueController(service, mockk())

        // when
        controller.getQueueById(token, queueId)

        // then
        verify(exactly = 1) { service.getQueueById(token, queueId) }
    }

    @Test
    fun `Test getQueueInviteCode service called`() {
        // given
        val token = "token"
        val queueId = 1L
        val service = mockk<QueueService>(relaxed = true)
        val controller = QueueController(service, mockk())

        // when
        controller.getQueueInviteCode(token, queueId)

        // then
        verify(exactly = 1) { service.getQueueInviteCode(token, queueId) }
    }

    @Test
    fun `Test createQueue service called`() {
        // given
        val token = "token"
        val queueDto = NewQueueDto(
            name = "name",
            color = "color",
            trackExpenses = false
        )
        val service = mockk<QueueService>(relaxed = true)
        val controller = QueueController(service, mockk())

        // when
        controller.createQueue(token, queueDto)

        // then
        verify(exactly = 1) { service.createQueue(token, queueDto) }
    }

    @Test
    fun `Test editQueue service called`() {
        // given
        val token = "token"
        val queueId = 1L
        val queueDto = EditQueueDto(
            name = "name",
            color = "color",
            trackExpenses = false,
            participants = null
        )
        val service = mockk<QueueService>(relaxed = true)
        val controller = QueueController(service, mockk())

        // when
        controller.editQueue(token, queueId, queueDto)

        // then
        verify(exactly = 1) { service.editQueue(token, queueId, queueDto) }
    }

    @Test
    fun `Test freezeUnFreezeQueue service called`() {
        // given
        val token = "token"
        val queueId = 1L
        val service = mockk<QueueService>(relaxed = true)
        val controller = QueueController(service, mockk())

        // when
        controller.changeQueueActivity(token, queueId, QueueActivityDto(false))

        // then
        verify(exactly = 1) { service.freezeUnFreezeQueue(token, queueId, false) }
    }

    @Test
    fun `Test deleteQueue service called`() {
        // given
        val token = "token"
        val queueId = 1L
        val service = mockk<QueueService>(relaxed = true)
        val controller = QueueController(service, mockk())

        // when
        controller.deleteQueue(token, queueId)

        // then
        verify(exactly = 1) { service.deleteQueue(token, queueId) }
    }

    @Test
    fun `Test joinQueue service called`() {
        // given
        val token = "token"
        val queueDto = QueueInviteCodeDto(
            pinCode = "pin code",
            qrCode = null
        )
        val service = mockk<QueueService>(relaxed = true)
        val controller = QueueController(service, mockk())

        // when
        controller.joinQueue(token, queueDto)

        // then
        verify(exactly = 1) { service.joinQueue(token, queueDto) }
    }

    @Test
    fun `Test shakeUser service called`() {
        // given
        val token = "token"
        val queueId = 1L
        val service = mockk<QueueService>(relaxed = true)
        val controller = QueueController(service, mockk())

        // when
        controller.shakeUser(token, queueId)

        // then
        verify(exactly = 1) { service.shakeUser(token, queueId) }
    }

    @Test
    fun `Test getTasks service called`() {
        // given
        val token = "token"
        val service = mockk<ToDoTaskService>(relaxed = true)
        val controller = QueueController(mockk(), service)

        // when
        controller.getToDoTasks(token)

        // then
        verify(exactly = 1) { service.getToDoTasks(token) }
    }

    @Test
    fun `Test completeTask service called`() {
        // given
        val token = "token"
        val queueId = 1L
        val expensesDTO = ExpensesDto(1000L)
        val service = mockk<ToDoTaskService>(relaxed = true)
        val controller = QueueController(mockk(), service)

        // when
        controller.completeTask(token, queueId, expensesDTO)

        // then
        verify(exactly = 1) { service.completeTask(token, queueId, expensesDTO.expenses) }
    }

    @Test
    fun `Test skipTask service called`() {
        // given
        val token = "token"
        val queueId = 1L
        val service = mockk<ToDoTaskService>(relaxed = true)
        val controller = QueueController(mockk(), service)

        // when
        controller.skipTask(token, queueId)

        // then
        verify(exactly = 1) { service.skipTask(token, queueId) }
    }
}
