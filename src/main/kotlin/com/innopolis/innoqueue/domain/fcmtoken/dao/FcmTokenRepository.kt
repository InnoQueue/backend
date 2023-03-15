package com.innopolis.innoqueue.domain.fcmtoken.dao

import com.innopolis.innoqueue.domain.fcmtoken.model.FcmToken
import com.innopolis.innoqueue.domain.fcmtoken.model.FcmTokenId
import org.springframework.data.repository.CrudRepository

interface FcmTokenRepository : CrudRepository<FcmToken, FcmTokenId>
