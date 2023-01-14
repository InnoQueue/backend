package com.innopolis.innoqueue.domain.notification.service

import com.innopolis.innoqueue.domain.fcmtoken.service.FcmTokenService
import com.innopolis.innoqueue.domain.firebase.service.FirebaseMessagingNotificationsService
import com.innopolis.innoqueue.domain.notification.dao.NotificationRepository
import com.innopolis.innoqueue.domain.notification.dto.NotificationDto
import com.innopolis.innoqueue.domain.notification.dto.NotificationsListDto
import com.innopolis.innoqueue.domain.notification.enums.NotificationsType
import com.innopolis.innoqueue.domain.notification.model.Notification
import com.innopolis.innoqueue.domain.queue.dao.QueueRepository
import com.innopolis.innoqueue.domain.user.model.User
import com.innopolis.innoqueue.domain.user.service.UserService
import com.innopolis.innoqueue.domain.userqueue.dao.UserQueueRepository
import com.innopolis.innoqueue.domain.userqueue.model.UserQueue
import com.innopolis.innoqueue.rest.v0.dto.EmptyDto
import com.innopolis.innoqueue.rest.v0.dto.NewNotificationDto
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
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
class NotificationService(
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
    @Transactional
    fun getNotifications(token: String): NotificationsListDto {
        val (allNotifications, unreadNotifications) = notificationRepository.findAllByToken(token)
            .partition { it.isRead!! }
        unreadNotifications.readNotifications()
        return NotificationsListDto(
            unreadNotifications = unreadNotifications.toNotificationDTO(),
            allNotifications = allNotifications.toNotificationDTO()
        )
    }

    /**
     * Returns boolean whether there is any unread notification
     * @param token - user token
     */
    @Transactional
    fun anyNewNotification(token: String): NewNotificationDto =
        NewNotificationDto(notificationRepository.anyUnreadNotification(token))

    /**
     * Deletes notifications older than 2 weeks
     */
    @Transactional
    fun clearOldNotifications(): EmptyDto {
        notificationRepository.deleteAll(notificationRepository.findAllExpiredNotifications())
        return EmptyDto(CLEAR_RESPONSE)
    }

    /**
     * Saves notification in database and sends it via firebase
     */
    @Transactional
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
        NotificationDto(
            notificationId = it.id!!,
            messageType = it.messageType!!,
            participantId = it.participantId,
            participantName = if (it.participantId == null) DELETED_USER_NAME else userService
                .findUserNameById(it.participantId!!) ?: DELETED_USER_NAME,
            queueId = it.queueId,
            queueName = if (it.queueId == null) DELETED_QUEUE_NAME else queueRepository
                .findByIdOrNull(it.queueId!!)?.name
                ?: DELETED_QUEUE_NAME,
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
                        recipientUserId = it.userQueueId?.userId!!,
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
        userService.findUserById(this.userQueueId?.userId!!)!!.isUserSubscribed(notificationsType, participantId)
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
