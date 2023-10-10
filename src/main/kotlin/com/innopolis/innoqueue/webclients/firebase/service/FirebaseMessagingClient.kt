package com.innopolis.innoqueue.webclients.firebase.service

import com.innopolis.innoqueue.webclients.firebase.model.FirebaseMessage

interface FirebaseMessagingClient {
    /**
     * Sends a particular message via Firebase
     */
    fun sendFirebaseMessage(firebaseMessage: FirebaseMessage)
}
