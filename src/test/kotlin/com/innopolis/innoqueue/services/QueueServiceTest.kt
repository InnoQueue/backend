package com.innopolis.innoqueue.services

import com.innopolis.innoqueue.dao.QueuePinCodeRepository
import com.innopolis.innoqueue.dao.QueueQrCodeRepository
import com.innopolis.innoqueue.dao.QueueRepository
import com.innopolis.innoqueue.dao.UserQueueRepository
import com.innopolis.innoqueue.dto.EditQueueDTO
import com.innopolis.innoqueue.dto.NewQueueDTO
import com.innopolis.innoqueue.dto.QueueInviteCodeDTO
import com.innopolis.innoqueue.dto.UserExpensesDTO
import com.innopolis.innoqueue.testcontainers.PostgresTestContainer
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.jdbc.Sql

class QueueServiceTest : PostgresTestContainer() {

    @Autowired
    private lateinit var queueService: QueueService

    @Autowired
    private lateinit var queueRepository: QueueRepository

    @Autowired
    private lateinit var userQueueRepository: UserQueueRepository

    @Autowired
    private lateinit var queuePinCodeRepository: QueuePinCodeRepository

    @Autowired
    private lateinit var queueQrCodeRepository: QueueQrCodeRepository

    @Test
    fun `Test getQueues userQueueRepository is called`() {
        // given
        val token = "token"
        val userService = mockk<UserService>(relaxed = true)
        val notificationsService = mockk<NotificationsService>(relaxed = true)
        val userQueueRepo = mockk<UserQueueRepository>(relaxed = true)
        every { userQueueRepo.findAllUserQueueByToken(token) } returns emptyList()
        val queueRepo = mockk<QueueRepository>(relaxed = true)
        val queuePinCodeRepo = mockk<QueuePinCodeRepository>(relaxed = true)
        val queueQrCodeRepo = mockk<QueueQrCodeRepository>(relaxed = true)
        val service =
            QueueService(userService, notificationsService, userQueueRepo, queueRepo, queuePinCodeRepo, queueQrCodeRepo)

        // when
        service.getQueues(token)

        // then
        verify(exactly = 1) { userQueueRepo.findAllUserQueueByToken(token) }
    }

    @Test
    @Sql("users.sql", "user_settings.sql", "queues2.sql", "user_queue3.sql")
    fun `Test getQueues activeQueues`() {
        // given
        val token = "11111"

        // when
        val activeQueues = queueService.getQueues(token).activeQueues

        // then
        assertEquals(4, activeQueues.size)

        assertEquals(44L, activeQueues[0].queueId)
        assertEquals("Bring Water", activeQueues[0].queueName)
        assertEquals("BLUE", activeQueues[0].queueColor)

        assertEquals(40L, activeQueues[1].queueId)
        assertEquals("Buy Soap", activeQueues[1].queueName)
        assertEquals("ORANGE", activeQueues[1].queueColor)

        assertEquals(46L, activeQueues[2].queueId)
        assertEquals("Buy Sponge", activeQueues[2].queueName)
        assertEquals("PURPLE", activeQueues[2].queueColor)

        assertEquals(39L, activeQueues[3].queueId)
        assertEquals("Trash", activeQueues[3].queueName)
        assertEquals("YELLOW", activeQueues[3].queueColor)
    }

    @Test
    @Sql("users.sql", "user_settings.sql", "queues2.sql", "user_queue3.sql")
    fun `Test getQueues frozenQueues`() {
        // given
        val token = "11111"

        // when
        val frozenQueues = queueService.getQueues(token).frozenQueues

        // then
        assertEquals(2, frozenQueues.size)

        assertEquals(34L, frozenQueues[0].queueId)
        assertEquals("Buy Dishwashing Soap", frozenQueues[0].queueName)
        assertEquals("GREEN", frozenQueues[0].queueColor)

        assertEquals(6L, frozenQueues[1].queueId)
        assertEquals("Buy Toilet Paper", frozenQueues[1].queueName)
        assertEquals("RED", frozenQueues[1].queueColor)
    }

    @Test
    @Sql("users.sql", "user_settings.sql", "queues2.sql", "user_queue3.sql")
    fun `Test getQueues activeQueues are sorted by name`() {
        // given
        val token = "11111"

        // when
        val activeQueues = queueService.getQueues(token).activeQueues

        // then
        assertTrue(
            activeQueues.sortedBy { it.queueName }.zip(activeQueues).all { it.first.queueId == it.second.queueId })
    }

    @Test
    @Sql("users.sql", "user_settings.sql", "queues2.sql", "user_queue3.sql")
    fun `Test getQueues frozenQueues are sorted by name`() {
        // given
        val token = "11111"

        // when
        val frozenQueues = queueService.getQueues(token).frozenQueues

        // then
        assertTrue(
            frozenQueues.sortedBy { it.queueName }.zip(frozenQueues).all { it.first.queueId == it.second.queueId })
    }

    @Test
    @Sql("users.sql", "user_settings.sql", "queues2.sql", "user_queue3.sql")
    fun `Test getQueueById`() {
        // given
        val token = "11111"
        val queueId = 6L

        // when
        val result = queueService.getQueueById(token, queueId)

        // then
        assertEquals(queueId, result.queueId)
        assertEquals("Buy Toilet Paper", result.queueName)
        assertEquals("RED", result.queueColor)
        assertEquals(false, result.isYourTurn)
        assertEquals(true, result.trackExpenses)
        assertEquals(false, result.isActive)
        assertEquals(false, result.isAdmin)
        assertEquals(
            UserExpensesDTO(
                userId = 2L,
                userName = "Emil",
                expenses = 0.0,
                isActive = true
            ), result.currentUser
        )
        assertEquals(4, result.participants.size)
        assertEquals(
            UserExpensesDTO(
                userId = 3L,
                userName = "Roman",
                expenses = 0.0,
                isActive = true
            ), result.participants[0]
        )
        assertEquals(
            UserExpensesDTO(
                userId = 4L,
                userName = "Timur",
                expenses = 0.0,
                isActive = true
            ), result.participants[1]
        )
        assertEquals(
            UserExpensesDTO(
                userId = 5L,
                userName = "Ivan",
                expenses = 0.0,
                isActive = true
            ), result.participants[2]
        )
        assertEquals(
            UserExpensesDTO(
                userId = 1L,
                userName = "admin",
                expenses = 0.0,
                isActive = false
            ), result.participants[3]
        )
    }

    @Test
    @Sql("users.sql", "user_settings.sql", "queues2.sql", "user_queue3.sql")
    fun `Test getQueueInviteCode`() {
        // given
        val token = "11111"
        val queueId = 6L

        // when
        val result = queueService.getQueueInviteCode(token, queueId)

        // then
        val pinCodes = queuePinCodeRepository.findAll()
        val qrCodes = queueQrCodeRepository.findAll()
        assertTrue(pinCodes.any { it.pinCode == result.pinCode && it.queue?.id == queueId })
        assertTrue(qrCodes.any { it.qrCode == result.qrCode && it.queue?.id == queueId })
    }

    @Test
    @Sql("users.sql", "user_settings.sql")
    fun `Test createQueue`() {
        // given
        val token = "11111"
        val queueName = "queueName"
        val queueColor = "queueColor"
        val trackExpenses = true
        val queueDto = NewQueueDTO(
            name = queueName,
            color = queueColor,
            trackExpenses = trackExpenses
        )

        // when
        val responseDto = queueService.createQueue(token, queueDto)

        // then
        val queue = queueRepository.findAll().toList()
        val userQueue = userQueueRepository.findAll().toList()

        assertEquals(1, queue.size)
        assertEquals(1, userQueue.size)

        assertEquals(queue[0].id, responseDto.queueId)
        assertEquals(queue[0].name, responseDto.queueName)
        assertEquals(queue[0].color, responseDto.queueColor)
        assertEquals(queue[0].currentUser?.id, responseDto.currentUser.userId)
        assertEquals(queue[0].trackExpenses, responseDto.trackExpenses)
        assertEquals(userQueue[0].isActive, responseDto.currentUser.isActive)
        assertEquals(userQueue[0].isActive, responseDto.isActive)

        assertEquals(queueName, responseDto.queueName)
        assertEquals(queueColor, responseDto.queueColor)
        assertEquals(trackExpenses, responseDto.trackExpenses)
        assertEquals(1L, responseDto.currentUser.userId)
        assertEquals(0, responseDto.participants.size)
        assertEquals(true, responseDto.currentUser.isActive)
        assertEquals(true, responseDto.isYourTurn)
        assertEquals(true, responseDto.isActive)
        assertEquals(true, responseDto.isAdmin)
    }

    @Test
    @Sql("users.sql", "user_settings.sql", "queues2.sql", "user_queue3.sql")
    fun `Test editQueue`() {
        // given
        val token = "11111"
        val queueId = 40L
        val queueName = "new queue name"
        val queueColor = "new queue color"
        val trackExpenses = false
        val queueDto = EditQueueDTO(
            queueId = queueId,
            name = queueName,
            color = queueColor,
            trackExpenses = trackExpenses,
            participants = listOf(2L, 3L)
        )

        // when
        val responseDto = queueService.editQueue(token, queueDto)

        // then
        val queue = queueRepository.findAll().first { it.id == queueId }
        val userQueueList = userQueueRepository.findAll().filter { it.queue?.id == queueId }.toList()

        assertEquals(queueName, responseDto.queueName)
        assertEquals(queueColor, responseDto.queueColor)
        assertEquals(trackExpenses, responseDto.trackExpenses)

        assertEquals(queue.name, responseDto.queueName)
        assertEquals(queue.color, responseDto.queueColor)
        assertEquals(queue.trackExpenses, responseDto.trackExpenses)
        assertEquals(2, responseDto.participants.size)
        assertEquals(3, userQueueList.filter { it.queue?.id == queueId }.size)
        assertTrue(userQueueList.filter { it.queue?.id == queueId }.map { it.user?.id }
            .all { it in listOf(1L, 2L, 3L) })
    }

    @Test
    @Sql("users.sql", "user_settings.sql", "queues.sql", "user_queue3.sql")
    fun `Test freeze queue`() {
        // given
        val token = "11111"
        val queueId = 46L

        // when
        queueService.freezeUnFreezeQueue(token, queueId, false)

        // then
        val userQueue =
            userQueueRepository.findAll().first { it.queue?.id == queueId && it.user?.id == 1L }

        assertEquals(false, userQueue.isActive)
    }

    @Test
    @Sql("users.sql", "user_settings.sql", "queues2.sql", "user_queue3.sql")
    fun `Test unfreeze queue`() {
        // given
        val token = "11111"
        val queueId = 34L

        // when
        queueService.freezeUnFreezeQueue(token, queueId, true)

        // then
        val userQueue =
            userQueueRepository.findAll().first { it.queue?.id == queueId && it.user?.id == 1L }

        assertEquals(true, userQueue.isActive)
    }

    @Test
    @Sql("users.sql", "user_settings.sql", "queues2.sql", "user_queue3.sql")
    fun `Test leave queue`() {
        // given
        val token = "11111"
        val queueId = 6L

        // when
        queueService.deleteQueue(token, queueId)

        // then
        val userQueues = userQueueRepository.findAll().filter { it.queue?.id == queueId }.toList()
        assertEquals(4, userQueues.size)
        assertTrue(userQueues.none { it.user?.id == 1L })
    }

    @Test
    @Sql("users.sql", "user_settings.sql", "queues2.sql", "user_queue3.sql")
    fun `Test delete queue`() {
        // given
        val token = "11111"
        val queueId = 34L

        // when
        queueService.deleteQueue(token, queueId)

        // then
        val userQueues = userQueueRepository.findAll().filter { it.queue?.id == queueId }.toList()
        val queues = queueRepository.findAll()
        assertEquals(0, userQueues.size)
        assertTrue(queues.none { it.id == queueId })
    }

    @Test
    @Sql("users.sql", "user_settings.sql", "queues2.sql", "queue_pin_code.sql")
    fun `Test joinQueue pin code`() {
        // given
        val token = "11111"
        val queueId = 6L
        val queueDto = QueueInviteCodeDTO(
            pinCode = "111111",
            qrCode = null
        )

        // when
        val result = queueService.joinQueue(token, queueDto)

        // then
        assertTrue(userQueueRepository.findAll().any { it.queue?.id == queueId && it.user?.id == 1L })
        assertEquals(queueId, result.queueId)
    }

    @Test
    @Sql("users.sql", "user_settings.sql", "queues2.sql", "queue_qr_code.sql")
    fun `Test joinQueue qr code`() {
        // given
        val token = "11111"
        val queueId = 6L
        val queueDto = QueueInviteCodeDTO(
            pinCode = null,
            qrCode = "111111"
        )

        // when
        val result = queueService.joinQueue(token, queueDto)

        // then
        assertTrue(userQueueRepository.findAll().any { it.queue?.id == queueId && it.user?.id == 1L })
        assertEquals(queueId, result.queueId)
    }

    @Test
    @Sql("users.sql", "user_settings.sql", "queues2.sql", "user_queue3.sql")
    fun `Test shakeUser`() {
        // given
        val token = "11111"
        val queueId = 34L

        // when
        queueService.shakeUser(token, queueId)

        // then
        val userQueue = userQueueRepository.findAll().first { it.queue?.id == queueId && it.user?.id == 2L }
        assertEquals(true, userQueue.isImportant)
    }
}
