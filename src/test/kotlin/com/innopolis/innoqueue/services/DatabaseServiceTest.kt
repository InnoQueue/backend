package com.innopolis.innoqueue.services

import com.innopolis.innoqueue.dao.QueuePinCodeRepository
import com.innopolis.innoqueue.dao.QueueQrCodeRepository
import com.innopolis.innoqueue.testcontainers.PostgresTestContainer
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
    private lateinit var queuePinCodeRepository: QueuePinCodeRepository

    @Autowired
    private lateinit var queueQrCodeRepository: QueueQrCodeRepository

    @Test
    fun `Test clearExpiredInviteCodes repos were called`() {
        // given
        val queuePinCodeRepo = mockk<QueuePinCodeRepository>(relaxed = true)
        every { queuePinCodeRepo.findAll(any()) } returns listOf()

        val queueQrCodeRepo = mockk<QueueQrCodeRepository>(relaxed = true)
        every { queueQrCodeRepo.findAll(any()) } returns listOf()

        val service = DatabaseService(queuePinCodeRepo, queueQrCodeRepo)

        // when
        service.clearExpiredInviteCodes()

        // then
        verify(exactly = 1) { queuePinCodeRepo.findAll(any()) }
        verify(exactly = 1) { queuePinCodeRepo.deleteAll(any()) }
        verify(exactly = 1) { queueQrCodeRepo.findAll(any()) }
        verify(exactly = 1) { queueQrCodeRepo.deleteAll(any()) }
    }

    @Test
    @Sql("users.sql", "queues.sql", "queue_pin_code.sql", "queue_qr_code.sql")
    fun `Test clearExpiredInviteCodes`() {
        // given
        val currentDateTime = LocalDateTime.now(ZoneOffset.UTC)
        val expiredPinCodesDateTime = currentDateTime.minusHours(1L)
        val expiredQrCodesDateTime = currentDateTime.minusDays(1L)

        // when
        databaseService.clearExpiredInviteCodes()

        // then
        val pinCodes = queuePinCodeRepository.findAll().toList()
        val qrCodes = queueQrCodeRepository.findAll().toList()
        assertEquals(2, pinCodes.size)
        assertEquals(3, qrCodes.size)
        assertTrue(pinCodes.all { it.dateCreated!! >= expiredPinCodesDateTime })
        assertTrue(qrCodes.all { it.dateCreated!! >= expiredQrCodesDateTime })
    }
}
