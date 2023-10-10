package com.innopolis.innoqueue.webclients.firebase.service.impl

import com.innopolis.innoqueue.domain.notification.enums.NotificationType
import com.innopolis.innoqueue.webclients.firebase.exception.NotSupportedMessageType
import com.innopolis.innoqueue.webclients.firebase.model.FirebaseRecipients
import com.innopolis.innoqueue.webclients.firebase.model.TitleBody
import com.innopolis.innoqueue.webclients.firebase.service.NotificationTextFormerService
import org.springframework.stereotype.Service

@Service
class NotificationTextFormerServiceImpl : NotificationTextFormerService {
    private val titleBodyMessages = getTitleBodyMessages()
    override fun getTitleBody(firebaseRecipients: FirebaseRecipients, isPersonal: Boolean): TitleBody =
        titleBodyMessages[NotificationTypeMapKey(
            notificationType = firebaseRecipients.notificationType,
            isPersonal = isPersonal
        )]?.let {
            TitleBody(
                title = it.title.replacePLaceHolders(firebaseRecipients),
                body = it.body.replacePLaceHolders(firebaseRecipients)
            )
        } ?: throw NotSupportedMessageType(
            "Message not supported for ${firebaseRecipients.notificationType}, $isPersonal"
        )

    @Suppress("LongMethod")
    private fun getTitleBodyMessages() = mapOf(
        NotificationTypeMapKey(
            notificationType = NotificationType.YOUR_TURN,
            isPersonal = true
        ) to TitleBody(
            title = "It' your turn!",
            body = "It’s now your turn in the queue {QUEUE_NAME}"
        ),
        NotificationTypeMapKey(
            notificationType = NotificationType.YOUR_TURN,
            isPersonal = false
        ) to TitleBody(
            title = "Next in {QUEUE_NAME}",
            body = "{ACTOR_NAME} is now responsible for the queue {QUEUE_NAME}"
        ),
        NotificationTypeMapKey(
            notificationType = NotificationType.COMPLETED,
            isPersonal = false
        ) to TitleBody(
            title = "Progress in {QUEUE_NAME}",
            body = "{ACTOR_NAME} completed {QUEUE_NAME}"
        ),
        NotificationTypeMapKey(
            notificationType = NotificationType.SKIPPED,
            isPersonal = false
        ) to TitleBody(
            title = "Skip in {QUEUE_NAME}",
            body = "{ACTOR_NAME} skipped his/her turn in the {QUEUE_NAME}"
        ),
        NotificationTypeMapKey(
            notificationType = NotificationType.SHOOK,
            isPersonal = true
        ) to TitleBody(
            title = "Don't forget about {QUEUE_NAME}",
            body = "You were shook by roommate to remind you that it is your turn in the {QUEUE_NAME}!"
        ),
        NotificationTypeMapKey(
            notificationType = NotificationType.FROZEN,
            isPersonal = false
        ) to TitleBody(
            title = "{ACTOR_NAME} frozen a queue",
            body = "{ACTOR_NAME} has frozen the queue {QUEUE_NAME}"
        ),
        NotificationTypeMapKey(
            notificationType = NotificationType.UNFROZEN,
            isPersonal = false
        ) to TitleBody(
            title = "{ACTOR_NAME} unfrozen a queue",
            body = "{ACTOR_NAME} has unfrozen the queue {QUEUE_NAME}"
        ),
        NotificationTypeMapKey(
            notificationType = NotificationType.JOINED_QUEUE,
            isPersonal = false
        ) to TitleBody(
            title = "Join in the queue",
            body = "{ACTOR_NAME} joined the queue {QUEUE_NAME}"
        ),
        NotificationTypeMapKey(
            notificationType = NotificationType.LEFT_QUEUE,
            isPersonal = false
        ) to TitleBody(
            title = "Left the queue",
            body = "{ACTOR_NAME} left the queue {QUEUE_NAME}"
        ),
        NotificationTypeMapKey(
            notificationType = NotificationType.DELETE_QUEUE,
            isPersonal = false
        ) to TitleBody(
            title = "The queue was deleted",
            body = "{ACTOR_NAME} deleted the queue {QUEUE_NAME}"
        ),
        NotificationTypeMapKey(
            notificationType = NotificationType.UPDATE,
            isPersonal = true
        ) to TitleBody(
            title = "Update your App!",
            body = "The new app version was released"
        ),
        NotificationTypeMapKey(
            notificationType = NotificationType.OTHER,
            isPersonal = true
        ) to TitleBody(
            title = "New notification",
            body = "You have new notification"
        )
    )

    private fun String.replacePLaceHolders(firebaseRecipients: FirebaseRecipients) = this
        .replace("{QUEUE_NAME}", firebaseRecipients.queue.name)
        .replace("{ACTOR_NAME}", firebaseRecipients.actor.name)
}

private data class NotificationTypeMapKey(
    private val notificationType: NotificationType,
    private val isPersonal: Boolean
)
