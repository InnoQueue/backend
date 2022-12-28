package com.innopolis.innoqueue.service

import com.innopolis.innoqueue.domain.queue.dao.QueueRepository
import com.innopolis.innoqueue.domain.queue.dao.specifications.QueuePinCodeExpiredSpecification
import com.innopolis.innoqueue.domain.queue.dao.specifications.QueueQrCodeExpiredSpecification
import com.innopolis.innoqueue.rest.v1.dto.EmptyDTO
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.time.ZoneOffset

private const val PIN_CODE_LIVE_TIME_HOURS: Long = 1L
private const val QR_CODE_LIVE_TIME_DAYS: Long = 1L

/**
 * Service for managing the database
 */
@Service
class DatabaseService(
    private val queueRepository: QueueRepository
) {
    /**
     * Clear expired invite codes
     */
    fun clearExpiredInviteCodes(): EmptyDTO {
        val currentDateTime = LocalDateTime.now(ZoneOffset.UTC)
        removeExpiredPinCodes(currentDateTime)
        removeExpiredQrCodes(currentDateTime)
        return EmptyDTO("Expired invite codes were deleted")
    }

    private fun removeExpiredPinCodes(currentDateTime: LocalDateTime) {
        val expiredPinCodesDateTime = currentDateTime.minusHours(PIN_CODE_LIVE_TIME_HOURS)
        val queuesWithExpiredPinCodes =
            queueRepository.findAll(QueuePinCodeExpiredSpecification(expiredPinCodesDateTime))
        queuesWithExpiredPinCodes.forEach {
            it.pinCode = null
            it.pinDateCreated = null
        }
        queueRepository.saveAll(queuesWithExpiredPinCodes)
    }

    private fun removeExpiredQrCodes(currentDateTime: LocalDateTime) {
        val expiredQrCodesDateTime = currentDateTime.minusDays(QR_CODE_LIVE_TIME_DAYS)
        val queuesWithExpiredQrCodes = queueRepository.findAll(QueueQrCodeExpiredSpecification(expiredQrCodesDateTime))
        queuesWithExpiredQrCodes.forEach {
            it.qrCode = null
            it.qrDateCreated = null
        }
        queueRepository.saveAll(queuesWithExpiredQrCodes)
    }
}
