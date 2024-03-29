package com.innopolis.innoqueue.webclients.firebase.service.impl

import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingException
import com.google.firebase.messaging.Message
import com.google.firebase.messaging.Notification
import com.innopolis.innoqueue.webclients.firebase.model.FirebaseMessage
import com.innopolis.innoqueue.webclients.firebase.model.TitleBody
import com.innopolis.innoqueue.webclients.firebase.service.FirebaseMessagingClient
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Service

@Primary
@Service
@ConditionalOnBean(FirebaseMessaging::class)
class FirebaseMessagingClientImpl(
    val firebaseMessaging: FirebaseMessaging
) : FirebaseMessagingClient {
    private val logger = LoggerFactory.getLogger(javaClass)
    override fun sendFirebaseMessage(firebaseMessage: FirebaseMessage) {
        try {
            val message = firebaseMessage.message()
            val response = firebaseMessaging.send(message)
            logger.info("Firebase response: $response")
        } catch (e: FirebaseMessagingException) {
            logger.error("Firebase exception: $e")
        }
    }

    private fun FirebaseMessage.message() = Message
        .builder()
        .setToken(token)
        .setNotification(titleBody.notification())
        .putAllData(
            mapOf(
                "title" to titleBody.title,
                "body" to titleBody.body,
                "queue_id" to queueId.toString(),
                "queue_name" to queueName,
                "participant_name" to participantName
            )
        )
        .build()

    private fun TitleBody.notification() = Notification
        .builder()
        .setTitle(title)
        .setBody(body)
        .build()
}
