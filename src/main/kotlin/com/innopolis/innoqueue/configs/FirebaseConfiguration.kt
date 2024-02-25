package com.innopolis.innoqueue.configs

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import org.json.JSONObject
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.io.ByteArrayInputStream
import java.io.IOException
import java.io.InputStream

/**
 * Bean configuration for the Firebase options
 */
@Configuration
//@ConditionalOnProperty(name = ["firebase"], matchIfMissing = true)
class FirebaseConfiguration {
    @Value("\${firebase}")
    private val gservicesConfig: String? = null

    /**
     * Bean which injects Firebase options
     */
    @Bean
    @Throws(IOException::class)
    fun provideFirebaseOptions(): FirebaseApp? {
        return if (gservicesConfig == "null") {
            null
        } else {
            val jsonObject = JSONObject(gservicesConfig.toString())
            val inputStream: InputStream = ByteArrayInputStream(jsonObject.toString().toByteArray())
            val googleCredentials = GoogleCredentials
                .fromStream(inputStream)
            val firebaseOptions = FirebaseOptions
                .builder()
                .setCredentials(googleCredentials)
                .build()
            FirebaseApp.initializeApp(firebaseOptions, "InnoQueue")
        }
    }
}
