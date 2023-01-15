package com.innopolis.innoqueue.domain.queue.service

import com.innopolis.innoqueue.domain.notification.service.NotificationService
import com.innopolis.innoqueue.domain.queue.dao.QueueRepository
import com.innopolis.innoqueue.domain.queue.dto.EditQueueDto
import com.innopolis.innoqueue.domain.queue.dto.NewQueueDto
import com.innopolis.innoqueue.domain.queue.dto.QueueInviteCodeDto
import com.innopolis.innoqueue.domain.queue.dto.UserExpensesDto
import com.innopolis.innoqueue.domain.user.service.UserService
import com.innopolis.innoqueue.domain.userqueue.dao.UserQueueRepository
import com.innopolis.innoqueue.testcontainer.PostgresTestContainer
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions
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

    @Test
    fun `Test getQueues userQueueRepository is called`() {
        // given
        val token = "token"
        val userService = mockk<UserService>(relaxed = true)
        val notificationService = mockk<NotificationService>(relaxed = true)
        val userQueueRepo = mockk<UserQueueRepository>(relaxed = true)
        every { userQueueRepo.findAllUserQueueByToken(token) } returns emptyList()
        val queueRepo = mockk<QueueRepository>(relaxed = true)
        val service = QueueService(userService, notificationService, userQueueRepo, queueRepo)

        // when
        service.getQueues(token)

        // then
        verify(exactly = 1) { userQueueRepo.findAllUserQueueByToken(token) }
    }

    @Test
    @Sql("users.sql", "queues2.sql", "user_queue3.sql")
    fun `Test getQueues`() {
        // given
        val token = "11111"

        // when
        val queues = queueService.getQueues(token).queues

        // then
        assertEquals(6, queues.size)

        with(queues[0]) {
            assertEquals(44L, queueId)
            assertEquals("Bring Water", queueName)
            assertEquals("BLUE", queueColor)
            assertEquals("admin", onDutyUser)
            assertEquals(true, active)
        }

        with(queues[1]) {
            assertEquals(34L, queueId)
            assertEquals("Buy Dishwashing Soap", queueName)
            assertEquals("GREEN", queueColor)
            assertEquals("Emil", onDutyUser)
            assertEquals(false, active)
        }

        with(queues[2]) {
            assertEquals(40L, queueId)
            assertEquals("Buy Soap", queueName)
            assertEquals("ORANGE", queueColor)
            assertEquals("admin", onDutyUser)
            assertEquals(true, active)
        }

        with(queues[3]) {
            assertEquals(46L, queueId)
            assertEquals("Buy Sponge", queueName)
            assertEquals("PURPLE", queueColor)
            assertEquals("admin", onDutyUser)
            assertEquals(true, active)
        }

        with(queues[4]) {
            assertEquals(6L, queueId)
            assertEquals("Buy Toilet Paper", queueName)
            assertEquals("RED", queueColor)
            assertEquals("Emil", onDutyUser)
            assertEquals(false, active)
        }

        with(queues[5]) {
            assertEquals(39L, queueId)
            assertEquals("Trash", queueName)
            assertEquals("YELLOW", queueColor)
            assertEquals("admin", onDutyUser)
            assertEquals(true, active)
        }
    }

    @Test
    @Sql("users.sql", "queues2.sql", "user_queue3.sql")
    fun `Test getQueues are sorted by name`() {
        // given
        val token = "11111"

        // when
        val queues = queueService.getQueues(token).queues

        // then
        assertTrue(queues.sortedBy { it.queueName }.zip(queues).all { it.first.queueId == it.second.queueId })
    }

    @Test
    @Sql("users.sql", "queues2.sql", "user_queue2.sql")
    fun `Test getQueueById exception if queue does not exist`() {
        // given
        val token = "11111"
        val queueId = 4444L

        // when and then
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            queueService.getQueueById(token, queueId)
        }
    }

    @Test
    @Sql("users.sql", "queues2.sql")
    fun `Test getQueueById exception if user does not belong to queue`() {
        // given
        val token = "11111"
        val queueId = 6L

        // when and then
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            queueService.getQueueById(token, queueId)
        }
    }

    @Test
    @Sql("users.sql", "queues2.sql", "user_queue3.sql")
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
            UserExpensesDto(
                userId = 2L,
                userName = "Emil",
                expenses = 0L,
                active = true
            ), result.currentUser
        )
        assertEquals(4, result.participants.size)
        assertEquals(
            UserExpensesDto(
                userId = 3L,
                userName = "Roman",
                expenses = 0L,
                active = true
            ), result.participants[0]
        )
        assertEquals(
            UserExpensesDto(
                userId = 4L,
                userName = "Timur",
                expenses = 0L,
                active = true
            ), result.participants[1]
        )
        assertEquals(
            UserExpensesDto(
                userId = 5L,
                userName = "Ivan",
                expenses = 0L,
                active = true
            ), result.participants[2]
        )
        assertEquals(
            UserExpensesDto(
                userId = 1L,
                userName = "admin",
                expenses = 0L,
                active = false
            ), result.participants[3]
        )
    }

    @Test
    @Sql("users.sql", "queues2.sql")
    fun `Test getQueueInviteCode exception if user does not belong to queue`() {
        // given
        val token = "11111"
        val queueId = 6L

        // when and then
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            queueService.getQueueInviteCode(token, queueId)
        }
    }

    @Test
    @Sql("users.sql", "queues2.sql", "user_queue3.sql")
    fun `Test getQueueInviteCode`() {
        // given
        val token = "11111"
        val queueId = 6L

        // when
        val result = queueService.getQueueInviteCode(token, queueId)

        // then
        val pinCodes = queueRepository.findAll().filter { it.pinCode != null }
        val qrCodes = queueRepository.findAll().filter { it.qrCode != null }
        assertTrue(pinCodes.any { it.pinCode == result.pinCode && it?.queueId == queueId })
        assertTrue(qrCodes.any { it.qrCode == result.qrCode && it?.queueId == queueId })
    }

    @Test
    @Sql("users.sql")
    fun `Test createQueue`() {
        // given
        val token = "11111"
        val queueName = "queueName"
        val queueColor = "queueColor"
        val trackExpenses = true
        val queueDto = NewQueueDto(
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

        assertEquals(queue[0].queueId, responseDto.queueId)
        assertEquals(queue[0].name, responseDto.queueName)
        assertEquals(queue[0].color, responseDto.queueColor)
        assertEquals(queue[0].currentUserId, responseDto.currentUser.userId)
        assertEquals(queue[0].trackExpenses, responseDto.trackExpenses)
        assertEquals(userQueue[0].isActive, responseDto.currentUser.active)
        assertEquals(userQueue[0].isActive, responseDto.isActive)

        assertEquals(queueName, responseDto.queueName)
        assertEquals(queueColor, responseDto.queueColor)
        assertEquals(trackExpenses, responseDto.trackExpenses)
        assertEquals(1L, responseDto.currentUser.userId)
        assertEquals(0, responseDto.participants.size)
        assertEquals(true, responseDto.currentUser.active)
        assertEquals(true, responseDto.isYourTurn)
        assertEquals(true, responseDto.isActive)
        assertEquals(true, responseDto.isAdmin)
    }

    @Test
    @Sql("users.sql", "queues2.sql", "user_queue2.sql")
    fun `Test editQueue exception if user is not admin`() {
        // given
        val token = "11111"
        val queueDto = EditQueueDto(
            name = "queueName",
            color = "queueColor",
            trackExpenses = true,
            participants = null
        )

        // when and then
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            queueService.editQueue(token, 6L, queueDto)
        }
    }

    @Test
    @Sql("users.sql", "queues2.sql", "user_queue2.sql")
    fun `Test editQueue exception if queue does not exist`() {
        // given
        val token = "11111"
        val queueDto = EditQueueDto(
            name = "queueName",
            color = "queueColor",
            trackExpenses = true,
            participants = null
        )

        // when and then
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            queueService.editQueue(token, 6542L, queueDto)
        }
    }

    @Test
    @Sql("users.sql", "queues2.sql", "user_queue2.sql")
    fun `Test editQueue exception if queue name is an empty string`() {
        // given
        val token = "11111"
        val queueDto = EditQueueDto(
            name = "",
            color = "queueColor",
            trackExpenses = true,
            participants = null
        )

        // when and then
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            queueService.editQueue(token, 34L, queueDto)
        }
    }

    @Test
    @Sql("users.sql", "queues2.sql", "user_queue2.sql")
    fun `Test editQueue exception if queue color is an empty string`() {
        // given
        val token = "11111"
        val queueDto = EditQueueDto(
            name = "name",
            color = "",
            trackExpenses = true,
            participants = null
        )

        // when and then
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            queueService.editQueue(token, 34L, queueDto)
        }
    }

    @Test
    @Sql("users.sql", "queues2.sql", "user_queue3.sql")
    fun `Test editQueue`() {
        // given
        val token = "11111"
        val queueId = 40L
        val queueName = "new queue name"
        val queueColor = "new queue color"
        val trackExpenses = false
        val queueDto = EditQueueDto(
            name = queueName,
            color = queueColor,
            trackExpenses = trackExpenses,
            participants = listOf(2L, 3L)
        )

        // when
        val responseDto = queueService.editQueue(token, queueId, queueDto)

        // then
        val queue = queueRepository.findAll().first { it.queueId == queueId }
        val userQueueList = userQueueRepository.findAll().filter { it.userQueueId?.queueId == queueId }.toList()

        assertEquals(queueName, responseDto.queueName)
        assertEquals(queueColor, responseDto.queueColor)
        assertEquals(trackExpenses, responseDto.trackExpenses)

        assertEquals(queue.name, responseDto.queueName)
        assertEquals(queue.color, responseDto.queueColor)
        assertEquals(queue.trackExpenses, responseDto.trackExpenses)
        assertEquals(2, responseDto.participants.size)
        assertEquals(3, userQueueList.filter { it.userQueueId?.queueId == queueId }.size)
        assertTrue(userQueueList.filter { it.userQueueId?.queueId == queueId }.map { it.userQueueId?.userId }
            .all { it in listOf(1L, 2L, 3L) })
    }

    @Test
    @Sql("users.sql", "queues.sql", "user_queue3.sql")
    fun `Test freeze queue`() {
        // given
        val token = "11111"
        val queueId = 46L

        // when
        queueService.freezeUnFreezeQueue(token, queueId, false)

        // then
        val userQueue =
            userQueueRepository.findAll()
                .first { it.userQueueId?.queueId == queueId && it.userQueueId?.userId == 1L }

        assertEquals(false, userQueue.isActive)
    }

    @Test
    @Sql("users.sql", "queues2.sql", "user_queue3.sql")
    fun `Test unfreeze queue`() {
        // given
        val token = "11111"
        val queueId = 34L

        // when
        queueService.freezeUnFreezeQueue(token, queueId, true)

        // then
        val userQueue =
            userQueueRepository.findAll()
                .first { it.userQueueId?.queueId == queueId && it.userQueueId?.userId == 1L }

        assertEquals(true, userQueue.isActive)
    }

    @Test
    @Sql("users.sql", "queues2.sql", "user_queue3.sql")
    fun `Test leave queue`() {
        // given
        val token = "11111"
        val queueId = 6L

        // when
        queueService.deleteQueue(token, queueId)

        // then
        val userQueues = userQueueRepository.findAll().filter { it.userQueueId?.queueId == queueId }.toList()
        assertEquals(4, userQueues.size)
        assertTrue(userQueues.none { it.userQueueId?.userId == 1L })
    }

    @Test
    @Sql("users.sql", "queues2.sql", "user_queue3.sql")
    fun `Test delete queue`() {
        // given
        val token = "11111"
        val queueId = 34L

        // when
        queueService.deleteQueue(token, queueId)

        // then
        val userQueues = userQueueRepository.findAll().filter { it.userQueueId?.queueId == queueId }.toList()
        val queues = queueRepository.findAll()
        assertEquals(0, userQueues.size)
        assertTrue(queues.none { it.queueId == queueId })
    }

    @Test
    @Sql("users.sql", "queues2.sql", "user_queue2.sql")
    fun `Test joinQueue exception if nothing is provided`() {
        // given
        val token = "11111"
        val queueDto = QueueInviteCodeDto(
            pinCode = null,
            qrCode = null
        )

        // when and then
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            queueService.joinQueue(token, queueDto)
        }
    }

    @Test
    @Sql("users.sql", "queues2.sql", "user_queue2.sql")
    fun `Test joinQueue exception if pin code does not exist`() {
        // given
        val token = "11111"
        val queueDto = QueueInviteCodeDto(
            pinCode = "does not exist",
            qrCode = null
        )

        // when and then
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            queueService.joinQueue(token, queueDto)
        }
    }

    @Test
    @Sql("users.sql", "queues2.sql")
    fun `Test joinQueue pin code`() {
        // given
        val token = "11111"
        val queueId = 6L
        val queueDto = QueueInviteCodeDto(
            pinCode = "111111",
            qrCode = null
        )

        // when
        val result = queueService.joinQueue(token, queueDto)

        // then
        assertTrue(
            userQueueRepository.findAll()
                .any { it.userQueueId?.queueId == queueId && it.userQueueId?.userId == 1L })
        assertEquals(queueId, result.queueId)
    }

    @Test
    @Sql("users.sql", "queues2.sql", "user_queue2.sql")
    fun `Test joinQueue exception if qr code does not exist`() {
        // given
        val token = "11111"
        val queueDto = QueueInviteCodeDto(
            pinCode = null,
            qrCode = "does not exist"
        )

        // when and then
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            queueService.joinQueue(token, queueDto)
        }
    }

    @Test
    @Sql("users.sql", "queues2.sql")
    fun `Test joinQueue qr code`() {
        // given
        val token = "11111"
        val queueId = 6L
        val queueDto = QueueInviteCodeDto(
            pinCode = null,
            qrCode = "111111"
        )

        // when
        val result = queueService.joinQueue(token, queueDto)

        // then
        assertTrue(
            userQueueRepository.findAll()
                .any { it.userQueueId?.queueId == queueId && it.userQueueId?.userId == 1L })
        assertEquals(queueId, result.queueId)
    }

    @Test
    @Sql("users.sql", "queues2.sql", "user_queue3.sql")
    fun `Test shakeUser`() {
        // given
        val token = "11111"
        val queueId = 34L

        // when
        queueService.shakeUser(token, queueId)

        // then
        val queue = queueRepository.findAll().first { it?.queueId == queueId }
        assertEquals(true, queue.isImportant)
    }
}
