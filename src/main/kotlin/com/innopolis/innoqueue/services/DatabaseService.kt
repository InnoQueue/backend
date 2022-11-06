package com.innopolis.innoqueue.services

import com.innopolis.innoqueue.rest.v1.dto.EmptyDTO
import com.innopolis.innoqueue.dao.DatabaseRepository
import com.innopolis.innoqueue.dao.QueuePinCodeRepository
import com.innopolis.innoqueue.dao.QueueQrCodeRepository
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.time.ZoneOffset

private const val QR_CODE_LIVE_TIME_HOURS: Long = 24
private const val PIN_CODE_LIVE_TIME_MINUTES: Long = 60

@Service
class DatabaseService(
    private val databaseRepository: DatabaseRepository,
    private val queuePinCodeRepository: QueuePinCodeRepository,
    private val queueQrCodeRepository: QueueQrCodeRepository
) {
    fun resetDB(): EmptyDTO {
        try {
            databaseRepository.resetDB()
        } catch (_: Exception) {

        }
        return EmptyDTO("Database was reset by default data")
    }

    fun clearCodes(): EmptyDTO {
        val currentTime = LocalDateTime.now(ZoneOffset.UTC)

        val expiredPinCodes =
            queuePinCodeRepository.findAll().filter { isPinCodeExpired(currentTime, it.dateCreated!!) }
        queuePinCodeRepository.deleteAll(expiredPinCodes)

        val expiredQrCodes = queueQrCodeRepository.findAll().filter { isQrCodeExpired(currentTime, it.dateCreated!!) }
        queueQrCodeRepository.deleteAll(expiredQrCodes)

        return EmptyDTO("Expired invite codes were deleted")
    }

    private fun isPinCodeExpired(currentTime: LocalDateTime, dateCreated: LocalDateTime): Boolean {
        val dateExpired = dateCreated.plusMinutes(PIN_CODE_LIVE_TIME_MINUTES)
        return currentTime > dateExpired
    }

    private fun isQrCodeExpired(currentTime: LocalDateTime, dateCreated: LocalDateTime): Boolean {
        val dateExpired = dateCreated.plusHours(QR_CODE_LIVE_TIME_HOURS)
        return currentTime > dateExpired
    }
}
