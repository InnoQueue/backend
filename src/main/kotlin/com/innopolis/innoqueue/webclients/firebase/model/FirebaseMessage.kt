package com.innopolis.innoqueue.webclients.firebase.model

data class FirebaseMessage(
    val token: String,
    val titleBody: TitleBody,
    val queueId: Long,
    val queueName: String,
    val participantName: String
)
