package com.innopolis.innoqueue.domain.queue.service

import com.innopolis.innoqueue.domain.queue.dao.QueueRepository
import com.innopolis.innoqueue.domain.queue.model.Queue
import com.innopolis.innoqueue.testcontainer.PostgresTestContainer
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.jdbc.Sql
import java.time.LocalDateTime
import java.time.ZoneOffset

class DatabaseServiceTest : PostgresTestContainer() {

    @Autowired
    private lateinit var databaseService: DatabaseService

    @Autowired
    private lateinit var queueRepository: QueueRepository

    @Test
    fun `Test clearExpiredInviteCodes repos were called`() {
        // given
        val queues = emptyList<Queue>()
        val queueRepo = mockk<QueueRepository>(relaxed = true)
        every { queueRepo.findAll(any()) } returns queues

        val service = DatabaseService(queueRepo)

        // when
        service.clearExpiredInviteCodes()

        // then
        verify(exactly = 2) { queueRepo.findAll(any()) }
        verify(exactly = 2) { queueRepo.saveAll(queues) }
    }

    @Test
    @Sql("users.sql", "queues.sql")
    fun `Test clearExpiredInviteCodes`() {
        // given
        val currentDateTime = LocalDateTime.now(ZoneOffset.UTC)
        val expiredPinCodesDateTime = currentDateTime.minusHours(1L)
        val expiredQrCodesDateTime = currentDateTime.minusDays(1L)

        // when
        databaseService.clearExpiredInviteCodes()

        // then
        val pinCodes = queueRepository.findAll().filter { it.pinCode != null }.toList()
        val qrCodes = queueRepository.findAll().filter { it.qrCode != null }.toList()
        assertEquals(2, pinCodes.size)
        assertEquals(3, qrCodes.size)
        assertTrue(pinCodes.all { it.pinDateCreated!! >= expiredPinCodesDateTime })
        assertTrue(qrCodes.all { it.qrDateCreated!! >= expiredQrCodesDateTime })
    }
}
