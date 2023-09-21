package com.innopolis.innoqueue.webclients.firebase.util

import com.innopolis.innoqueue.domain.notification.enums.NotificationType

/**
 * Util class for building notification message text
 */
class MessagePushNotificationCreator(
    private val notificationType: NotificationType,
    private val queueName: String,
    private val isPersonal: Boolean,
    private val participantName: String
) {
    /**
     * Method prepares title and body text for a notification
     */
    fun getTitleAndBodyForMessage(): Pair<String?, String?> {
        val title = prepareTitleForNotificationMessage(notificationType, participantName, queueName, isPersonal)
        val body = prepareBodyForNotificationMessage(notificationType, participantName, queueName, isPersonal)
        return title to body
    }

    @Suppress("ComplexMethod")
    private fun prepareTitleForNotificationMessage(
        notificationType: NotificationType, participantName: String, queueName: String, isPersonal: Boolean
    ): String? {
        return when (notificationType) {
            NotificationType.YOUR_TURN -> if (isPersonal) {
                "It' your turn!"
            } else {
                "Next in $queueName"
            }

            NotificationType.COMPLETED -> if (isPersonal) {
                null
            } else {
                "Progress in $queueName"
            }

            NotificationType.SKIPPED -> if (isPersonal) {
                null
            } else {
                "Skip in $queueName"
            }

            NotificationType.SHOOK -> if (isPersonal) {
                "Don't forget about $queueName"
            } else {
                null
            }

            NotificationType.FROZEN -> if (isPersonal) {
                null
            } else {
                "$participantName frozen a queue"
            }

            NotificationType.UNFROZEN -> if (isPersonal) {
                null
            } else {
                "$participantName unfrozen a queue"
            }

            NotificationType.JOINED_QUEUE -> if (isPersonal) {
                null
            } else {
                "Join in the queue"
            }

            NotificationType.LEFT_QUEUE -> if (isPersonal) {
                null
            } else {
                "Left the queue"
            }

            NotificationType.DELETE_QUEUE -> if (isPersonal) {
                null
            } else {
                "The queue was deleted"
            }

            NotificationType.UPDATE -> "Update your App!"

            NotificationType.OTHER -> "New notification"
        }
    }

    @Suppress("ComplexMethod")
    private fun prepareBodyForNotificationMessage(
        notificationType: NotificationType, participantName: String, queueName: String, isPersonal: Boolean
    ): String? {
        return when (notificationType) {
            NotificationType.YOUR_TURN -> if (isPersonal) {
                "It’s now your turn in queue $queueName"
            } else {
                "$participantName is now responsible for a queue $queueName"
            }

            NotificationType.COMPLETED -> if (isPersonal) {
                null
            } else {
                "$participantName completed $queueName"
            }

            NotificationType.SKIPPED -> if (isPersonal) {
                null
            } else {
                "$participantName skipped his/her turn in $queueName"
            }

            NotificationType.SHOOK -> if (isPersonal) {
                "You were shook by roommate to remind you that it is your turn in $queueName!"
            } else {
                null
            }

            NotificationType.FROZEN -> if (isPersonal) {
                null
            } else {
                "$participantName has frozen a queue $queueName"
            }

            NotificationType.UNFROZEN -> if (isPersonal) {
                null
            } else {
                "$participantName has unfrozen a queue $queueName"
            }

            NotificationType.JOINED_QUEUE -> if (isPersonal) {
                "$participantName joined in queue $queueName"
            } else {
                null
            }

            NotificationType.LEFT_QUEUE -> if (isPersonal) {
                null
            } else {
                "$participantName left the queue $queueName"
            }

            NotificationType.DELETE_QUEUE -> if (isPersonal) {
                null
            } else {
                "$participantName deleted the queue $queueName"
            }

            NotificationType.UPDATE -> "Update your App!"

            NotificationType.OTHER -> "New notification"
        }
    }
}
