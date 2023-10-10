package com.innopolis.innoqueue.webclients.firebase.model

import com.innopolis.innoqueue.domain.notification.enums.NotificationType

data class FirebaseRecipients(
    val notificationType: NotificationType,
    val queue: Queue,
    val actor: Actor,
    val recipients: List<Recipient>
)

data class Queue(
    val id: Long,
    val name: String
)

data class Actor(
    val id: Long,
    val name: String
)

data class Recipient(
    val id: Long,
    val fcmTokens: List<String>
)
