package com.innopolis.innoqueue.domain.fcmtoken.service.impl

import com.innopolis.innoqueue.domain.fcmtoken.dao.FcmTokenRepository
import com.innopolis.innoqueue.domain.fcmtoken.model.FcmToken
import com.innopolis.innoqueue.domain.fcmtoken.model.FcmTokenId
import com.innopolis.innoqueue.domain.fcmtoken.service.FcmTokenService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.time.ZoneOffset

/**
 * Service for working with the fcm token model
 */
@Service
class FcmTokenServiceImpl(
    private val fcmTokenRepository: FcmTokenRepository
) : FcmTokenService {

    @Transactional
    override fun saveFcmToken(userId: Long, userFcmToken: String) {
        val fcmToken = FcmToken().apply {
            this.fcmTokenId = FcmTokenId().apply {
                this.userId = userId
                this.fcmToken = userFcmToken
            }
            this.dateCreated = LocalDateTime.now(ZoneOffset.UTC)
        }
        fcmTokenRepository.save(fcmToken)
    }

    @Transactional
    override fun findTokensForUser(userId: Long): List<String> =
        fcmTokenRepository.findAll().filter { it.fcmTokenId?.userId == userId }.map { it.fcmTokenId?.fcmToken!! }
}
