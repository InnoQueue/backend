package com.innopolis.innoqueue.webclients.firebase.service.impl

import com.google.firebase.messaging.FirebaseMessaging
import com.innopolis.innoqueue.webclients.firebase.model.FirebaseMessage
import com.innopolis.innoqueue.webclients.firebase.service.FirebaseMessagingClient
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.stereotype.Service

@Service
@ConditionalOnMissingBean(FirebaseMessaging::class)
class MockFirebaseMessagingClientImpl : FirebaseMessagingClient {
    private val logger = LoggerFactory.getLogger(javaClass)
    override fun sendFirebaseMessage(firebaseMessage: FirebaseMessage) {
        logger.info("Mock firebase was called. Nothing will be send")
    }
}
