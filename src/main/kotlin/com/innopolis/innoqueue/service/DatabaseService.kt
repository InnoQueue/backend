package com.innopolis.innoqueue.service

import com.innopolis.innoqueue.controller.dto.EmptyDTO
import com.innopolis.innoqueue.repository.DatabaseRepository
import com.innopolis.innoqueue.repository.QueuePinCodeRepository
import com.innopolis.innoqueue.repository.QueueQrCodeRepository
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.time.ZoneOffset

@Service
class DatabaseService(
    private val databaseRepository: DatabaseRepository,
    private val queuePinCodeRepository: QueuePinCodeRepository,
    private val queueQrCodeRepository: QueueQrCodeRepository
) {
    private val pinCodeLiveTimeMinutes: Long = 60
    private val qrCodeLiveTimeHours: Long = 24
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
        val dateExpired = dateCreated.plusMinutes(pinCodeLiveTimeMinutes)
        return currentTime > dateExpired
    }

    private fun isQrCodeExpired(currentTime: LocalDateTime, dateCreated: LocalDateTime): Boolean {
        val dateExpired = dateCreated.plusHours(qrCodeLiveTimeHours)
        return currentTime > dateExpired
    }
}
