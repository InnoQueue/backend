package com.innopolis.innoqueue.domain.fcmtoken.service

interface FcmTokenService {
    fun saveFcmToken(userId: Long, userFcmToken: String)

    fun findTokensForUser(userId: Long): List<String>
}
