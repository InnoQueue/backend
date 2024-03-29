package com.innopolis.innoqueue.domain.external.service.impl

import com.innopolis.innoqueue.domain.external.dto.HostDto
import com.innopolis.innoqueue.domain.external.service.DatabaseService
import com.innopolis.innoqueue.domain.queue.dao.QueueRepository
import com.innopolis.innoqueue.domain.queue.dao.specification.QueuePinCodeExpiredSpecification
import com.innopolis.innoqueue.domain.queue.dao.specification.QueueQrCodeExpiredSpecification
import com.innopolis.innoqueue.rest.v1.dto.EmptyDto
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.time.ZoneOffset

private const val PIN_CODE_LIVE_TIME_HOURS: Long = 1L
private const val QR_CODE_LIVE_TIME_DAYS: Long = 1L

/**
 * Service for managing the database
 */
@Service
class DatabaseServiceImpl(
    private val queueRepository: QueueRepository,
    @Value("\${server.host.dev}")
    private val devHost: String? = null,
    @Value("\${server.host.prod}")
    private val prodHost: String? = null
) : DatabaseService {
    private val logger = LoggerFactory.getLogger(javaClass)

    /**
     * Clear expired invite codes
     */
    @Transactional
    override fun clearExpiredInviteCodes(): EmptyDto {
        logger.info("Clear expired invite codes")
        val currentDateTime = LocalDateTime.now(ZoneOffset.UTC)
        removeExpiredPinCodes(currentDateTime)
        removeExpiredQrCodes(currentDateTime)
        return EmptyDto("Expired invite codes were deleted")
    }

    @Transactional
    override fun getHost() = HostDto(devHost!!, prodHost!!)

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
