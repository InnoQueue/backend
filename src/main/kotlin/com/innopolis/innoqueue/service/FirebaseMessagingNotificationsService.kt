package com.innopolis.innoqueue.service

import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingException
import com.google.firebase.messaging.Message
import com.google.firebase.messaging.Notification
import org.springframework.stereotype.Service

@Service
//@ConditionalOnBean(name = ["firebaseApp"])
class FirebaseMessagingNotificationsService(
    firebaseApp: FirebaseApp?
) {
    private val firebaseMessaging: FirebaseMessaging? = firebaseApp?.let { FirebaseMessaging.getInstance(it) }

    @Throws(FirebaseMessagingException::class)
    fun sendNotification(title: String?, body: String?, token: String?, dataMap: HashMap<String, String?>): String {
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
