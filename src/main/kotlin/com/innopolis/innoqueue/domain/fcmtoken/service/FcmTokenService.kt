package com.innopolis.innoqueue.domain.fcmtoken.service

import com.innopolis.innoqueue.domain.fcmtoken.dao.FcmTokenRepository
import com.innopolis.innoqueue.domain.fcmtoken.model.FcmToken
import com.innopolis.innoqueue.domain.fcmtoken.model.FcmTokenId
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.time.ZoneOffset

/**
 * Service for working with the fcm token model
 */
@Service
class FcmTokenService(
    private val fcmTokenRepository: FcmTokenRepository
) {

    fun saveFcmToken(userId: Long, userFcmToken: String) {
        val fcmToken = FcmToken().apply {
            this.fcmTokenId = FcmTokenId().apply {
                this.userId = userId
                this.fcmToken = userFcmToken
            }
            this.dateCreated = LocalDateTime.now(ZoneOffset.UTC)
        }
        fcmTokenRepository.save(fcmToken)
    }

    fun findTokensForUser(userId: Long): List<String> =
        fcmTokenRepository.findAll().filter { it.fcmTokenId?.userId == userId }.map { it.fcmTokenId?.fcmToken!! }
}
