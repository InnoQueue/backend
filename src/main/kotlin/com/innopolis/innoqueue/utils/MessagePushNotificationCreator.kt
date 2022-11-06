package com.innopolis.innoqueue.utils

import com.innopolis.innoqueue.enums.NotificationsType

class MessagePushNotificationCreator(
    private val notificationType: NotificationsType,
    private val queueName: String,
    private val isPersonal: Boolean,
    private val participantName: String
) {
    fun getTitleAndBodyForMessage(): Pair<String?, String?> {
        val title = prepareTitleForNotificationMessage(notificationType, participantName, queueName, isPersonal)
        val body = prepareBodyForNotificationMessage(notificationType, participantName, queueName, isPersonal)
        return title to body
    }

    @Suppress("ComplexMethod")
    private fun prepareTitleForNotificationMessage(
        notificationType: NotificationsType, participantName: String, queueName: String, isPersonal: Boolean
    ): String? {
        return when (notificationType) {
            NotificationsType.YOUR_TURN -> if (isPersonal) {
                "It' your turn!"
            } else {
                "Next in $queueName"
            }

            NotificationsType.COMPLETED -> if (isPersonal) {
                null
            } else {
                "Progress in $queueName"
            }

            NotificationsType.SKIPPED -> if (isPersonal) {
                null
            } else {
                "Skip in $queueName"
            }

            NotificationsType.SHOOK -> if (isPersonal) {
                "Don't forget about $queueName"
            } else {
                null
            }

            NotificationsType.FROZEN -> if (isPersonal) {
                null
            } else {
                "$participantName frozen a queue"
            }

            NotificationsType.UNFROZEN -> if (isPersonal) {
                null
            } else {
                "$participantName unfrozen a queue"
            }

            NotificationsType.JOINED_QUEUE -> if (isPersonal) {
                null
            } else {
                "Join in the queue"
            }

            NotificationsType.LEFT_QUEUE -> if (isPersonal) {
                null
            } else {
                "Left the queue"
            }

            NotificationsType.DELETE_QUEUE -> if (isPersonal) {
                null
            } else {
                "The queue was deleted"
            }
        }
    }

    @Suppress("ComplexMethod")
    private fun prepareBodyForNotificationMessage(
        notificationType: NotificationsType, participantName: String, queueName: String, isPersonal: Boolean
    ): String? {
        return when (notificationType) {
            NotificationsType.YOUR_TURN -> if (isPersonal) {
                "It’s now your turn in queue $queueName"
            } else {
                "$participantName is now responsible for a queue $queueName"
            }

            NotificationsType.COMPLETED -> if (isPersonal) {
                null
            } else {
                "$participantName completed $queueName"
            }

            NotificationsType.SKIPPED -> if (isPersonal) {
                null
            } else {
                "$participantName skipped his/her turn in $queueName"
            }

            NotificationsType.SHOOK -> if (isPersonal) {
                "You were shook by roommate to remind you that it is your turn in $queueName!"
            } else {
                null
            }

            NotificationsType.FROZEN -> if (isPersonal) {
                null
            } else {
                "$participantName has frozen a queue $queueName"
            }

            NotificationsType.UNFROZEN -> if (isPersonal) {
                null
            } else {
                "$participantName has unfrozen a queue $queueName"
            }

            NotificationsType.JOINED_QUEUE -> if (isPersonal) {
                "$participantName joined in queue $queueName"
            } else {
                null
            }

            NotificationsType.LEFT_QUEUE -> if (isPersonal) {
                null
            } else {
                "$participantName left the queue $queueName"
            }

            NotificationsType.DELETE_QUEUE -> if (isPersonal) {
                null
            } else {
                "$participantName deleted the queue $queueName"
            }
        }
    }
}
