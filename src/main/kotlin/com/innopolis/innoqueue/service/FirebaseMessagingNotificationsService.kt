package com.innopolis.innoqueue.service

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingException
import com.google.firebase.messaging.Message
import com.google.firebase.messaging.Notification
import org.springframework.core.io.ClassPathResource
import org.springframework.stereotype.Service
import java.io.IOException

@Service
class FirebaseMessagingNotificationsService {
    private val firebaseMessaging: FirebaseMessaging

    @Throws(IOException::class)
    private fun firebaseMessaging(): FirebaseMessaging {
        val googleCredentials = GoogleCredentials
            .fromStream(ClassPathResource("innoqueue-firebase.json").inputStream)
        val firebaseOptions = FirebaseOptions
            .builder()
            .setCredentials(googleCredentials)
            .build()
        val app = FirebaseApp.initializeApp(firebaseOptions, "InnoQueue")
        return FirebaseMessaging.getInstance(app)
    }

    init {
        firebaseMessaging = firebaseMessaging()
    }

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
        return firebaseMessaging.send(message)
    }
}
