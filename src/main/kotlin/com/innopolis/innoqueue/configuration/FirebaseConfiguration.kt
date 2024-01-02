package com.innopolis.innoqueue.configuration

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.messaging.FirebaseMessaging
import org.json.JSONObject
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.io.ByteArrayInputStream
import java.io.InputStream

private const val FIREBASE_APP_NAME = "InnoQueue"

/**
 * Bean configuration for the Firebase options
 */
@Configuration
class FirebaseConfiguration {
    @Value("\${firebase}")
    private val googleServicesConfig: String? = null

    /**
     * Bean which injects Firebase options if GOOGLE_CREDENTIALS property was provided in the application.yml
     */
    @Bean
    @ConditionalOnProperty(name = ["firebase"])
    fun firebaseApp(): FirebaseApp {
        val jsonObject = JSONObject(googleServicesConfig.toString())
        val inputStream: InputStream = ByteArrayInputStream(jsonObject.toString().toByteArray())
        val googleCredentials = GoogleCredentials
            .fromStream(inputStream)
        val firebaseOptions = FirebaseOptions
            .builder()
            .setCredentials(googleCredentials)
            .build()
        return FirebaseApp.initializeApp(firebaseOptions, FIREBASE_APP_NAME)
    }

    /**
     * Bean which injects FirebaseMessaging for sending push notifications.
     * It will be created only if GOOGLE_CREDENTIALS property was provided in the application.yml
     */
    @Bean
    @ConditionalOnBean(FirebaseApp::class)
    fun firebaseMessaging(firebaseApp: FirebaseApp): FirebaseMessaging {
        return FirebaseMessaging.getInstance(firebaseApp)
    }
}
