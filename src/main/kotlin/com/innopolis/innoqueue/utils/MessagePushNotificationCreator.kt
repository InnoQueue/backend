package com.innopolis.innoqueue.utils

class MessagePushNotificationCreator(
    private val notificationType: NotificationsTypes,
    private val queueName: String,
    private val isPersonal: Boolean,
    private val participantName: String
) {
    fun getTitleAndBodyForMessage(): Pair<String?, String?> {
        val title = prepareTitleForNotificationMessage(notificationType, participantName, queueName, isPersonal)
        val body = prepareBodyForNotificationMessage(notificationType, participantName, queueName, isPersonal)
        return title to body
    }

    private fun prepareTitleForNotificationMessage(
        notificationType: NotificationsTypes, participantName: String, queueName: String, isPersonal: Boolean
    ): String? {
        return when (notificationType) {
            NotificationsTypes.YOUR_TURN -> if (isPersonal) {
                "It' your turn!"
            } else {
                "Next in $queueName"
            }
            NotificationsTypes.COMPLETED -> if (isPersonal) {
                null
            } else {
                "Progress in $queueName"
            }
            NotificationsTypes.SKIPPED -> if (isPersonal) {
                null
            } else {
                "Skip in $queueName"
            }
            NotificationsTypes.SHOOK -> if (isPersonal) {
                "Don't forget about $queueName"
            } else {
                null
            }
            NotificationsTypes.FROZEN -> if (isPersonal) {
                null
            } else {
                "$participantName frozen a queue"
            }
            NotificationsTypes.UNFROZEN -> if (isPersonal) {
                null
            } else {
                "$participantName unfrozen a queue"
            }
            NotificationsTypes.JOINED_QUEUE -> if (isPersonal) {
                null
            } else {
                "Join in the queue"
            }
            NotificationsTypes.LEFT_QUEUE -> if (isPersonal) {
                null
            } else {
                "Left the queue"
            }
            NotificationsTypes.DELETE_QUEUE -> if (isPersonal) {
                null
            } else {
                "The queue was deleted"
            }
        }
    }

    private fun prepareBodyForNotificationMessage(
        notificationType: NotificationsTypes, participantName: String, queueName: String, isPersonal: Boolean
    ): String? {
        return when (notificationType) {
            NotificationsTypes.YOUR_TURN -> if (isPersonal) {
                "It’s now your turn in queue $queueName"
            } else {
                "$participantName is now responsible for a queue $queueName"
            }
            NotificationsTypes.COMPLETED -> if (isPersonal) {
                null
            } else {
                "$participantName completed $queueName"
            }
            NotificationsTypes.SKIPPED -> if (isPersonal) {
                null
            } else {
                "$participantName skipped his/her turn in $queueName"
            }
            NotificationsTypes.SHOOK -> if (isPersonal) {
                "You were shook by roommate to remind you that it is your turn in $queueName!"
            } else {
                null
            }
            NotificationsTypes.FROZEN -> if (isPersonal) {
                null
            } else {
                "$participantName has frozen a queue $queueName"
            }
            NotificationsTypes.UNFROZEN -> if (isPersonal) {
                null
            } else {
                "$participantName has unfrozen a queue $queueName"
            }
            NotificationsTypes.JOINED_QUEUE -> if (isPersonal) {
                "$participantName joined in queue $queueName"
            } else {
                null
            }
            NotificationsTypes.LEFT_QUEUE -> if (isPersonal) {
                null
            } else {
                "$participantName left the queue $queueName"
            }
            NotificationsTypes.DELETE_QUEUE -> if (isPersonal) {
                null
            } else {
                "$participantName deleted the queue $queueName"
            }
        }
    }
}
