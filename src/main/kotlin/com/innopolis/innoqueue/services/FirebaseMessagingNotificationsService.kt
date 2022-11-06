package com.innopolis.innoqueue.services

import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingException
import com.google.firebase.messaging.Message
import com.google.firebase.messaging.Notification
import com.innopolis.innoqueue.enums.NotificationsType
import com.innopolis.innoqueue.utils.MessagePushNotificationCreator
import org.springframework.stereotype.Service

@Service
//@ConditionalOnBean(name = ["firebaseApp"])
class FirebaseMessagingNotificationsService(
    firebaseApp: FirebaseApp?
) {
    private val firebaseMessaging: FirebaseMessaging? = firebaseApp?.let { FirebaseMessaging.getInstance(it) }

    fun sendNotificationsToFirebase(
        addressees: List<Pair<Long, String?>>,
        notificationType: NotificationsType,
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
        if (title != null && body != null && addressee.second != null) {
            try {
                val dataMap = HashMap<String, String?>()
                dataMap["title"] = title
                dataMap["body"] = body
                dataMap["queue_id"] = queue.first.toString()
                dataMap["queue_name"] = queue.second
                dataMap["participant_name"] = participant.second
                val res = sendNotification(title, body, addressee.second, dataMap)
                println("Firebase result: $res")
            } catch (e: FirebaseMessagingException) {
                println("Firebase exception: $e")
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
