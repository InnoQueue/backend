package com.innopolis.innoqueue.domain.notification.listener

import com.innopolis.innoqueue.domain.fcmtoken.service.FcmTokenService
import com.innopolis.innoqueue.webclients.firebase.model.Actor
import com.innopolis.innoqueue.webclients.firebase.model.FirebaseRecipients
import com.innopolis.innoqueue.webclients.firebase.model.Queue
import com.innopolis.innoqueue.webclients.firebase.model.Recipient
import com.innopolis.innoqueue.webclients.firebase.service.FirebaseMessagingService
import org.springframework.stereotype.Component
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener

@Component
class SendNotificationEventListener(
    private val firebaseMessagingService: FirebaseMessagingService,
    private val fcmTokenService: FcmTokenService
) {
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    fun sendFirebaseNotification(notificationEvent: SendNotificationEvent) {
        firebaseMessagingService.sendNotificationsToFirebase(
            FirebaseRecipients(
                notificationType = notificationEvent.notificationType,
                queue = Queue(
                    id = notificationEvent.notificationMessageDto.queueId,
                    name = notificationEvent.notificationMessageDto.queueName
                ),
                actor = Actor(
                    id = notificationEvent.notificationMessageDto.participantId,
                    name = notificationEvent.notificationMessageDto.participantName
                ),
                recipients = notificationEvent.notifications
                    .mapNotNull { it.user }
                    .map {
                        Recipient(
                            id = it.id!!,
                            fcmTokens = fcmTokenService.findTokensForUser(it.id!!)
                        )
                    }
            )
        )
    }
}
