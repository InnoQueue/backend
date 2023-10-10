package com.innopolis.innoqueue.webclients.firebase.service

import com.innopolis.innoqueue.webclients.firebase.model.FirebaseRecipients

interface FirebaseMessagingService {
    fun sendNotificationsToFirebase(firebaseRecipients: FirebaseRecipients)
}
