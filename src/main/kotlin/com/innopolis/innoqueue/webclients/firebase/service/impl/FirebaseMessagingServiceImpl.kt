package com.innopolis.innoqueue.webclients.firebase.service.impl

import com.innopolis.innoqueue.webclients.firebase.exception.NotSupportedMessageType
import com.innopolis.innoqueue.webclients.firebase.model.FirebaseMessage
import com.innopolis.innoqueue.webclients.firebase.model.FirebaseRecipients
import com.innopolis.innoqueue.webclients.firebase.model.Recipient
import com.innopolis.innoqueue.webclients.firebase.service.FirebaseMessagingClient
import com.innopolis.innoqueue.webclients.firebase.service.FirebaseMessagingService
import com.innopolis.innoqueue.webclients.firebase.service.NotificationTextFormerService
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

/**
 * Service for working with Firebase
 */
@Service
class FirebaseMessagingServiceImpl(
    private val firebaseMessagingClient: FirebaseMessagingClient,
    private val notificationTextFormerService: NotificationTextFormerService
) : FirebaseMessagingService {
    private val logger = LoggerFactory.getLogger(javaClass)

    override fun sendNotificationsToFirebase(firebaseRecipients: FirebaseRecipients) {
        val firebaseMessages = firebaseRecipients.firebaseMessages()
        // TODO send messages by chunks via quartz
        firebaseMessages.forEach { firebaseMessagingClient.sendFirebaseMessage(it) }
    }

    private fun FirebaseRecipients.firebaseMessages(): List<FirebaseMessage> {
        val (personalRecipients, nonPersonalRecipients) = recipients.partition { it.id == actor.id }
        val personalFirebaseMessages = personalRecipients.toFirebaseMessages(this, true)
        val nonPersonalFirebaseMessages = nonPersonalRecipients.toFirebaseMessages(this, false)
        return personalFirebaseMessages + nonPersonalFirebaseMessages
    }

    private fun List<Recipient>.toFirebaseMessages(
        firebaseRecipients: FirebaseRecipients,
        isPersonal: Boolean
    ): List<FirebaseMessage> = try {
        val titleBody = notificationTextFormerService.getTitleBody(firebaseRecipients, isPersonal)
        this.flatMap { recipient ->
            recipient.fcmTokens.map { fcmToken ->
                FirebaseMessage(
                    token = fcmToken,
                    titleBody = titleBody,
                    queueId = firebaseRecipients.queue.id,
                    queueName = firebaseRecipients.queue.name,
                    participantName = firebaseRecipients.actor.name
                )
            }
        }
    } catch (e: NotSupportedMessageType) {
        logger.error(e.message)
        emptyList()
    }
}
