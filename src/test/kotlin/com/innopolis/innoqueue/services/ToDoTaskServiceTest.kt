package com.innopolis.innoqueue.services

import com.innopolis.innoqueue.dao.QueueRepository
import com.innopolis.innoqueue.dao.UserQueueRepository
import com.innopolis.innoqueue.testcontainers.PostgresTestContainer
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.jdbc.Sql

class ToDoTaskServiceTest : PostgresTestContainer() {

    @Autowired
    private lateinit var toDoTaskService: ToDoTaskService

    @Autowired
    private lateinit var userQueueRepository: UserQueueRepository

    @Test
    fun `Test getToDoTasks queueRepository is called`() {
        // given
        val token = "token"
        val userService = mockk<UserService>(relaxed = true)
        val queueService = mockk<QueueService>(relaxed = true)
        val notificationsService = mockk<NotificationsService>(relaxed = true)
        val queueRepo = mockk<QueueRepository>(relaxed = true)
        every { queueRepo.findToDoTasks(token) } returns emptyList()
        val userQueueRepo = mockk<UserQueueRepository>(relaxed = true)
        val service = ToDoTaskService(userService, queueService, notificationsService, queueRepo, userQueueRepo)

        // when
        service.getToDoTasks(token)

        // then
        verify(exactly = 1) { queueRepo.findToDoTasks(token) }
    }

    @Test
    @Sql("users.sql", "user_settings.sql", "queues2.sql", "user_queue2.sql")
    fun `Test getToDoTasks method`() {
        // given
        val token = "11111"

        // when
        val result = toDoTaskService.getToDoTasks(token)

        // then
        assertEquals(3, result.size)

        assertEquals(44L, result[0].queueId)
        assertEquals("Bring Water", result[0].name)
        assertEquals("BLUE", result[0].color)
        assertEquals(true, result[0].isImportant)
        assertEquals(false, result[0].trackExpenses)

        assertEquals(40L, result[1].queueId)
        assertEquals("Buy Soap", result[1].name)
        assertEquals("ORANGE", result[1].color)
        assertEquals(false, result[1].isImportant)
        assertEquals(true, result[1].trackExpenses)

        assertEquals(46L, result[2].queueId)
        assertEquals("Buy Sponge", result[2].name)
        assertEquals("PURPLE", result[2].color)
        assertEquals(false, result[2].isImportant)
        assertEquals(true, result[2].trackExpenses)
    }

    @Test
    @Sql("users.sql", "user_settings.sql", "queues2.sql", "user_queue2.sql")
    fun `Test getToDoTasks important todos first`() {
        // given
        val token = "token"

        // when
        val result = toDoTaskService.getToDoTasks(token)

        // then
        val (important, others) = result.partition { it.isImportant!! }
        val sortedResult = important + others

        assertTrue(result.zip(sortedResult).all { it.first.queueId == it.second.queueId })
    }

    @Test
    @Sql("users.sql", "user_settings.sql", "queues2.sql", "user_queue2.sql")
    fun `Test getToDoTasks sorted by queue name`() {
        // given
        val token = "token"

        // when
        val result = toDoTaskService.getToDoTasks(token)

        // then
        val (important, others) = result.partition { it.isImportant!! }

        assertTrue(important.zip(important.sortedBy { it.name!! }).all { it.first.queueId == it.second.queueId })
        assertTrue(others.zip(others.sortedBy { it.name!! }).all { it.first.queueId == it.second.queueId })
    }

    @Test
    @Sql("users.sql", "user_settings.sql", "queues2.sql", "user_queue2.sql")
    fun `Test getToDoTasks does not show todos with 1 participant`() {
        // given
        val token = "11111"
        val queueId = 39L

        // when
        val result = toDoTaskService.getToDoTasks(token)

        // then
        assertTrue(userQueueRepository.findAll().any { it.user?.token == token && it.queue?.id == queueId })
        assertTrue(result.none { it.queueId == queueId })
    }

    @Test
    @Sql("users.sql", "user_settings.sql", "queues2.sql", "user_queue2.sql")
    fun `Test completeTask expenses is null`() {
        // given
        val token = "11111"
        val taskId = 40L

        // when and then
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            toDoTaskService.completeTask(token, taskId, null)
        }
    }

    @Test
    @Sql("users.sql", "user_settings.sql", "queues2.sql", "user_queue2.sql")
    fun `Test completeTask expenses is negative`() {
        // given
        val token = "11111"
        val taskId = 40L

        // when and then
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            toDoTaskService.completeTask(token, taskId, -1.1)
        }
    }

    @Test
    @Sql("users.sql", "user_settings.sql", "queues2.sql", "user_queue2.sql")
    fun `Test completeTask is on duty`() {
        // given
        val token = "11111"
        val taskId = 40L

        // when
        toDoTaskService.completeTask(token, taskId, 0.0)

        // then
        assertTrue(toDoTaskService.getToDoTasks(token).none { it.queueId == taskId })
    }

    @Test
    @Sql("users.sql", "user_settings.sql", "queues2.sql", "user_queue2.sql")
    fun `Test completeTask add progress when not on duty`() {
        // given
        val token = "11111"
        val taskId = 34L

        // when
        toDoTaskService.completeTask(token, taskId, 0.0)

        // then
        assertEquals(-1, userQueueRepository.findAll().first { it.id == 7L }.skips)
    }

    @Test
    @Sql("users.sql", "user_settings.sql", "queues2.sql", "user_queue2.sql")
    fun `Test completeTask is on duty and has skips`() {
        // given
        val token = "11111"
        val taskId = 44L

        // when
        toDoTaskService.completeTask(token, taskId, 0.0)

        // then
        assertEquals(0, userQueueRepository.findAll().first { it.id == 1L }.skips)
    }

    @Test
    @Sql("users.sql", "user_settings.sql", "queues2.sql", "user_queue2.sql")
    fun `Test skipTask`() {
        // given
        val token = "11111"
        val taskId = 44L

        // when
        toDoTaskService.skipTask(token, taskId)

        // then
        assertEquals(2, userQueueRepository.findAll().first { it.id == 1L }.skips)
    }
}
