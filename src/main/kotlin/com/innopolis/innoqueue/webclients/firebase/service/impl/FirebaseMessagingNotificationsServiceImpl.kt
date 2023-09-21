package com.innopolis.innoqueue.webclients.firebase.service.impl

import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingException
import com.google.firebase.messaging.Message
import com.google.firebase.messaging.Notification
import com.innopolis.innoqueue.webclients.firebase.service.FirebaseMessagingNotificationsService
import com.innopolis.innoqueue.webclients.firebase.util.MessagePushNotificationCreator
import com.innopolis.innoqueue.domain.notification.enums.NotificationType
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * Service for working with Firebase
 */
@Service
//@ConditionalOnBean(name = ["firebaseApp"])
class FirebaseMessagingNotificationsServiceImpl(
    firebaseApp: FirebaseApp?
) : FirebaseMessagingNotificationsService {
    private val logger = LoggerFactory.getLogger(javaClass)
    private val firebaseMessaging: FirebaseMessaging? = firebaseApp?.let { FirebaseMessaging.getInstance(it) }

    @Suppress("NestedBlockDepth")
    /**
     * Sends a particular message via Firebase
     */
    @Transactional
    override fun sendNotificationsToFirebase(
        addressees: List<Pair<Long, List<String>>>,
        notificationType: NotificationType,
        participant: Pair<Long, String>,
        queue: Pair<Long, String>,
    ) = addressees.forEach { addressee ->
        val isPersonal = addressee.first == participant.first
        val (title, body) = MessagePushNotificationCreator(
            notificationType = notificationType,
            queueName = queue.second,
            isPersonal = isPersonal,
            participantName = participant.second
        ).getTitleAndBodyForMessage()
        if (title != null && body != null) {
            addressee.second.forEach {
                try {
                    val dataMap = HashMap<String, String?>()
                    dataMap["title"] = title
                    dataMap["body"] = body
                    dataMap["queue_id"] = queue.first.toString()
                    dataMap["queue_name"] = queue.second
                    dataMap["participant_name"] = participant.second
                    val res = sendNotification(title, body, it, dataMap)
                    logger.info("Firebase response: $res")
                } catch (e: FirebaseMessagingException) {
                    logger.info("Firebase exception: $e")
                }
            }
        }
    }

    @Throws(FirebaseMessagingException::class)
    private fun sendNotification(
        title: String,
        body: String,
        token: String?,
        dataMap: HashMap<String, String?>
    ): String {
        val notification = Notification
            .builder()
            .setTitle(title)
            .setBody(body)
            .build()
        val message = Message
            .builder()
            .setToken(token)
            .setNotification(notification)
            .putAllData(dataMap)
            .build()
        return firebaseMessaging?.send(message) ?: ""
    }
}
