package com.innopolis.innoqueue.services

import com.innopolis.innoqueue.dao.QueuePinCodeRepository
import com.innopolis.innoqueue.dao.QueueQrCodeRepository
import com.innopolis.innoqueue.dao.specifications.QueuePinCodeExpiredSpecification
import com.innopolis.innoqueue.dao.specifications.QueueQrCodeExpiredSpecification
import com.innopolis.innoqueue.rest.v1.dto.EmptyDTO
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.time.ZoneOffset

private const val PIN_CODE_LIVE_TIME_HOURS: Long = 1L
private const val QR_CODE_LIVE_TIME_DAYS: Long = 1L

@Service
class DatabaseService(
    private val queuePinCodeRepository: QueuePinCodeRepository,
    private val queueQrCodeRepository: QueueQrCodeRepository
) {
    fun clearExpiredInviteCodes(): EmptyDTO {
        val currentDateTime = LocalDateTime.now(ZoneOffset.UTC)
        removeExpiredPinCodes(currentDateTime)
        removeExpiredQrCodes(currentDateTime)
        return EmptyDTO("Expired invite codes were deleted")
    }

    private fun removeExpiredPinCodes(currentDateTime: LocalDateTime) {
        val expiredPinCodesDateTime = currentDateTime.minusHours(PIN_CODE_LIVE_TIME_HOURS)
        val expiredPinCodes = queuePinCodeRepository.findAll(QueuePinCodeExpiredSpecification(expiredPinCodesDateTime))
        queuePinCodeRepository.deleteAll(expiredPinCodes)
    }

    private fun removeExpiredQrCodes(currentDateTime: LocalDateTime) {
        val expiredQrCodesDateTime = currentDateTime.minusDays(QR_CODE_LIVE_TIME_DAYS)
        val expiredQrCodes = queueQrCodeRepository.findAll(QueueQrCodeExpiredSpecification(expiredQrCodesDateTime))
        queueQrCodeRepository.deleteAll(expiredQrCodes)
    }
}
