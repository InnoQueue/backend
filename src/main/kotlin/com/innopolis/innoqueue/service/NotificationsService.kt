package com.innopolis.innoqueue.service

import com.innopolis.innoqueue.dao.NotificationRepository
import com.innopolis.innoqueue.dao.UserQueueRepository
import com.innopolis.innoqueue.domain.fcmtoken.service.FcmTokenService
import com.innopolis.innoqueue.domain.queue.dao.QueueRepository
import com.innopolis.innoqueue.domain.user.model.User
import com.innopolis.innoqueue.domain.user.service.UserService
import com.innopolis.innoqueue.dto.NotificationDTO
import com.innopolis.innoqueue.dto.NotificationsListDTO
import com.innopolis.innoqueue.enums.NotificationsType
import com.innopolis.innoqueue.model.Notification
import com.innopolis.innoqueue.model.UserQueue
import com.innopolis.innoqueue.rest.v1.dto.EmptyDTO
import com.innopolis.innoqueue.rest.v1.dto.NewNotificationDTO
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.time.ZoneOffset

private const val DELETED_USER_NAME = "Deleted user"
private const val DELETED_QUEUE_NAME = "Deleted queue"
private const val CLEAR_RESPONSE = "Old notifications were deleted"

/**
 * Service for working with notifications
 */
@Suppress("TooManyFunctions")
@Service
class NotificationsService(
    private val firebaseMessagingService: FirebaseMessagingNotificationsService,
    private val userService: UserService,
    private val fcmTokenService: FcmTokenService,
    private val queueRepository: QueueRepository,
    private val userQueueRepository: UserQueueRepository,
    private val notificationRepository: NotificationRepository
) {

    /**
     * Lists all notifications
     * @param token - user token
     */
    fun getNotifications(token: String): NotificationsListDTO {
        val (allNotifications, unreadNotifications) = notificationRepository.findAllByToken(token)
            .partition { it.isRead!! }
        unreadNotifications.readNotifications()
        return NotificationsListDTO(
            unreadNotifications = unreadNotifications.toNotificationDTO(),
            allNotifications = allNotifications.toNotificationDTO()
        )
    }

    /**
     * Returns boolean whether there is any unread notification
     * @param token - user token
     */
    fun anyNewNotification(token: String): NewNotificationDTO =
        NewNotificationDTO(notificationRepository.anyUnreadNotification(token))

    /**
     * Deletes notifications older than 2 weeks
     */
    fun clearOldNotifications(): EmptyDTO {
        notificationRepository.deleteAll(notificationRepository.findAllExpiredNotifications())
        return EmptyDTO(CLEAR_RESPONSE)
    }

    /**
     * Saves notification in database and sends it via firebase
     */
    fun sendNotificationMessage(
        notificationType: NotificationsType,
        participantId: Long,
        participantName: String,
        queueId: Long,
        queueName: String
    ) {
        val notifications = prepareNotificationsListToSend(notificationType, participantId, queueId)
        notificationRepository.saveAll(notifications)
        firebaseMessagingService.sendNotificationsToFirebase(
            addressees = notifications
                .mapNotNull { it.user }
                .map { it.id!! to fcmTokenService.findTokensForUser(it.id!!) },
            notificationType = notificationType,
            participant = participantId to participantName,
            queue = queueId to queueName,
        )
    }

    private fun List<Notification>.readNotifications() {
        for (notification in this) {
            notification.isRead = true
        }
        notificationRepository.saveAll(this)
    }

    private fun List<Notification>.toNotificationDTO() = this.map {
        NotificationDTO(
            messageType = it.messageType!!,
            participantId = it.participantId!!,
            participantName = userService.findUserNameById(it.participantId!!) ?: DELETED_USER_NAME,
            queueId = it.queueId!!,
            queueName = queueRepository.findByIdOrNull(it.queueId!!)?.name ?: DELETED_QUEUE_NAME,
            date = it.date!!
        )
    }

    private fun prepareNotificationsListToSend(
        notificationType: NotificationsType,
        participantId: Long,
        queueId: Long,
    ): List<Notification> = when (notificationType) {
        NotificationsType.SHOOK -> {
            listOf(
                createNotification(
                    recipientUserId = participantId,
                    participantUserId = participantId,
                    notificationsType = notificationType,
                    referredQueueId = queueId
                )
            )
        }

        else -> {
            userQueueRepository.findUserQueueByQueueId(queueId)
                .filter { it.shouldSendMessage(notificationType, participantId) }
                .map {
                    createNotification(
                        recipientUserId = it.user?.id!!,
                        participantUserId = participantId,
                        notificationsType = notificationType,
                        referredQueueId = queueId
                    )
                }
        }
    }

    private fun createNotification(
        recipientUserId: Long,
        participantUserId: Long,
        notificationsType: NotificationsType,
        referredQueueId: Long
    ): Notification = Notification().apply {
        user = userService.findUserById(recipientUserId)
        participantId = participantUserId
        messageType = notificationsType
        queueId = referredQueueId
        isRead = false
        date = LocalDateTime.now(ZoneOffset.UTC)
    }

    private fun UserQueue.shouldSendMessage(
        notificationsType: NotificationsType,
        participantId: Long
    ): Boolean = if (notificationsType.isRequiredNotification()) {
        true
    } else {
        this.user!!.isUserSubscribed(notificationsType, participantId)
    }

    private fun NotificationsType.isRequiredNotification(): Boolean =
        when (this) {
            NotificationsType.SHOOK, NotificationsType.DELETE_QUEUE -> true
            else -> false
        }

    private fun User.isUserSubscribed(notificationType: NotificationsType, participantId: Long): Boolean =
        if (this.id == participantId) {
            true
        } else {
            when (notificationType) {
                NotificationsType.COMPLETED -> this.completed!!
                NotificationsType.SKIPPED -> this.skipped!!
                NotificationsType.JOINED_QUEUE -> this.joinedQueue!!
                NotificationsType.FROZEN, NotificationsType.UNFROZEN -> this.freeze!!
                NotificationsType.LEFT_QUEUE -> this.leftQueue!!
                NotificationsType.YOUR_TURN -> this.yourTurn!!
                else -> true
            }
        }
}
