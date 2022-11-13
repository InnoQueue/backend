package com.innopolis.innoqueue.rest.v1

import com.innopolis.innoqueue.dto.EditQueueDTO
import com.innopolis.innoqueue.dto.NewQueueDTO
import com.innopolis.innoqueue.dto.QueueInviteCodeDTO
import com.innopolis.innoqueue.services.QueueService
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test

class QueueControllerTest {

    @Test
    fun `Test getQueues service called`() {
        // given
        val token = "token"
        val service = mockk<QueueService>(relaxed = true)
        val controller = QueueController(service)

        // when
        controller.getQueues(token)

        // then
        verify(exactly = 1) { service.getQueues(token) }
    }

    @Test
    fun `Test getQueueById service called`() {
        // given
        val token = "token"
        val queueId = 1L
        val service = mockk<QueueService>(relaxed = true)
        val controller = QueueController(service)

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
        val controller = QueueController(service)

        // when
        controller.getQueueInviteCode(token, queueId)

        // then
        verify(exactly = 1) { service.getQueueInviteCode(token, queueId) }
    }

    @Test
    fun `Test createQueue service called`() {
        // given
        val token = "token"
        val queueDto = NewQueueDTO(
            name = "name",
            color = "color",
            trackExpenses = false
        )
        val service = mockk<QueueService>(relaxed = true)
        val controller = QueueController(service)

        // when
        controller.createQueue(token, queueDto)

        // then
        verify(exactly = 1) { service.createQueue(token, queueDto) }
    }

    @Test
    fun `Test editQueue service called`() {
        // given
        val token = "token"
        val queueDto = EditQueueDTO(
            name = "name",
            color = "color",
            trackExpenses = false,
            queueId = 1L,
            participants = null
        )
        val service = mockk<QueueService>(relaxed = true)
        val controller = QueueController(service)

        // when
        controller.editQueue(token, queueDto)

        // then
        verify(exactly = 1) { service.editQueue(token, queueDto) }
    }

    @Test
    fun `Test freezeQueue service called`() {
        // given
        val token = "token"
        val queueId = 1L
        val service = mockk<QueueService>(relaxed = true)
        val controller = QueueController(service)

        // when
        controller.freezeQueue(token, queueId)

        // then
        verify(exactly = 1) { service.freezeUnFreezeQueue(token, queueId, false) }
    }

    @Test
    fun `Test unfreezeQueue service called`() {
        // given
        val token = "token"
        val queueId = 1L
        val service = mockk<QueueService>(relaxed = true)
        val controller = QueueController(service)

        // when
        controller.unfreezeQueue(token, queueId)

        // then
        verify(exactly = 1) { service.freezeUnFreezeQueue(token, queueId, true) }
    }

    @Test
    fun `Test deleteQueue service called`() {
        // given
        val token = "token"
        val queueId = 1L
        val service = mockk<QueueService>(relaxed = true)
        val controller = QueueController(service)

        // when
        controller.deleteQueue(token, queueId)

        // then
        verify(exactly = 1) { service.deleteQueue(token, queueId) }
    }

    @Test
    fun `Test joinQueue service called`() {
        // given
        val token = "token"
        val queueDto = QueueInviteCodeDTO(
            pinCode = "pin code",
            qrCode = null
        )
        val service = mockk<QueueService>(relaxed = true)
        val controller = QueueController(service)

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
        val controller = QueueController(service)

        // when
        controller.shakeUser(token, queueId)

        // then
        verify(exactly = 1) { service.shakeUser(token, queueId) }
    }
}
