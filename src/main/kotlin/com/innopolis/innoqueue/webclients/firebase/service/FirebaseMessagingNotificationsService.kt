package com.innopolis.innoqueue.webclients.firebase.service

import com.innopolis.innoqueue.domain.notification.enums.NotificationType

interface FirebaseMessagingNotificationsService {
    fun sendNotificationsToFirebase(
        addressees: List<Pair<Long, List<String>>>,
        notificationType: NotificationType,
        participant: Pair<Long, String>,
        queue: Pair<Long, String>,
    )
}
