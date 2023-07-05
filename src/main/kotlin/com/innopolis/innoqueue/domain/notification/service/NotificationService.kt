package com.innopolis.innoqueue.domain.notification.service

import com.innopolis.innoqueue.domain.fcmtoken.service.FcmTokenService
import com.innopolis.innoqueue.domain.firebase.service.FirebaseMessagingNotificationsService
import com.innopolis.innoqueue.domain.notification.dao.NotificationRepository
import com.innopolis.innoqueue.domain.notification.dto.NotificationDto
import com.innopolis.innoqueue.domain.notification.enums.NotificationType
import com.innopolis.innoqueue.domain.notification.model.Notification
import com.innopolis.innoqueue.domain.queue.dao.QueueRepository
import com.innopolis.innoqueue.domain.user.model.User
import com.innopolis.innoqueue.domain.user.service.UserService
import com.innopolis.innoqueue.domain.userqueue.dao.UserQueueRepository
import com.innopolis.innoqueue.domain.userqueue.model.UserQueue
import com.innopolis.innoqueue.rest.v1.dto.EmptyDto
import com.innopolis.innoqueue.rest.v1.dto.NewNotificationDto
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
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
    private val logger = LoggerFactory.getLogger(javaClass)
    /**
     * Lists all notifications
     * @param token - user token
     */
    @Transactional
    fun getNotifications(token: String, pageable: Pageable): Page<NotificationDto> = notificationRepository
        .findAllByToken(token, pageable)
        .toNotificationDTO()

    /**
     * Returns boolean whether there is any unread notification
     * @param token - user token
     */
    @Transactional
    fun anyNewNotification(token: String): NewNotificationDto =
        NewNotificationDto(notificationRepository.anyUnreadNotification(token))

    /**
     * Marks notifications as read
     * @param token - user token
     */
    @Transactional
    fun readNotifications(token: String, notificationIds: List<Long>? = null) {
        logger.info("user-token=$token")
        val unreadNotifications = notificationRepository
            .findAllByToken(token)
            .filter { it.isRead == false }
            .let { allUnreadNotifications ->
                if (notificationIds == null) allUnreadNotifications
                else allUnreadNotifications.filter { it.id in notificationIds }
            }
        unreadNotifications.readNotifications()
    }

    /**
     * Delete notifications older than 2 weeks
     */
    @Transactional
    fun clearOldNotifications(): EmptyDto {
        notificationRepository.deleteAll(notificationRepository.findAllExpiredNotifications())
        return EmptyDto(CLEAR_RESPONSE)
    }

    /**
     * Delete specified by id notifications
     */
    @Transactional
    fun deleteNotifications(token: String, notificationIds: List<Long>? = null) {
        logger.info("user-token=$token")
        notificationRepository.deleteAll(
            notificationRepository
                .findAllByToken(token)
                .let { notifications ->
                    if (notificationIds == null) notifications
                    else notifications.filter { it.id in notificationIds }
                }
        )
    }

    /**
     * Delete notification by id
     */
    @Transactional
    fun deleteNotificationById(token: String, notificationId: Long) {
        logger.info("user-token=$token")
        notificationRepository
            .findAllByToken(token)
            .firstOrNull { it.id == notificationId }
            ?.let {
                notificationRepository.delete(it)
            }
    }

    /**
     * Saves notification in database and sends it via firebase
     */
    @Transactional
    fun sendNotificationMessage(
        notificationType: NotificationType,
        participantId: Long,
        participantName: String,
        queueId: Long,
        queueName: String
    ) {
        logger.info(
            "Sending notification: " +
                    "notificationType=$notificationType notificationType=$notificationType, queueId=$queueId"
        )
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

    private fun Page<Notification>.toNotificationDTO() = this.map {
        when (it.messageType!!) {
            NotificationType.UPDATE, NotificationType.OTHER -> NotificationDto(
                notificationId = it.id!!,
                messageType = it.messageType!!,
                message = it.message,
                participantId = null,
                participantName = null,
                queueId = null,
                queueName = null,
                date = it.date!!,
                read = it.isRead!!
            )

            else -> NotificationDto(
                notificationId = it.id!!,
                messageType = it.messageType!!,
                message = it.message,
                participantId = it.participantId,
                participantName = if (it.participantId == null) DELETED_USER_NAME else userService
                    .findUserNameById(it.participantId!!) ?: DELETED_USER_NAME,
                queueId = it.queueId,
                queueName = if (it.queueId == null) DELETED_QUEUE_NAME else queueRepository
                    .findByIdOrNull(it.queueId!!)?.name
                    ?: DELETED_QUEUE_NAME,
                date = it.date!!,
                read = it.isRead!!
            )
        }
    }

    private fun prepareNotificationsListToSend(
        notificationType: NotificationType,
        participantId: Long,
        queueId: Long,
    ): List<Notification> = when (notificationType) {
        NotificationType.SHOOK -> {
            listOf(
                createNotification(
                    recipientUserId = participantId,
                    participantUserId = participantId,
                    notificationType = notificationType,
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
                        notificationType = notificationType,
                        referredQueueId = queueId
                    )
                }
        }
    }

    private fun createNotification(
        recipientUserId: Long,
        participantUserId: Long,
        notificationType: NotificationType,
        referredQueueId: Long
    ): Notification = Notification().apply {
        user = userService.findUserById(recipientUserId)
        participantId = participantUserId
        messageType = notificationType
        queueId = referredQueueId
        isRead = false
        date = LocalDateTime.now(ZoneOffset.UTC)
    }

    private fun UserQueue.shouldSendMessage(
        notificationType: NotificationType,
        participantId: Long
    ): Boolean = if (notificationType.isRequiredNotification()) {
        true
    } else {
        userService.findUserById(this.userQueueId?.userId!!)!!.isUserSubscribed(notificationType, participantId)
    }

    private fun NotificationType.isRequiredNotification(): Boolean =
        when (this) {
            NotificationType.SHOOK,
            NotificationType.DELETE_QUEUE,
            NotificationType.UPDATE,
            NotificationType.OTHER -> true

            else -> false
        }

    private fun User.isUserSubscribed(notificationType: NotificationType, participantId: Long): Boolean =
        if (this.id == participantId) {
            true
        } else {
            when (notificationType) {
                NotificationType.COMPLETED -> this.completed!!
                NotificationType.SKIPPED -> this.skipped!!
                NotificationType.JOINED_QUEUE -> this.joinedQueue!!
                NotificationType.FROZEN, NotificationType.UNFROZEN -> this.freeze!!
                NotificationType.LEFT_QUEUE -> this.leftQueue!!
                NotificationType.YOUR_TURN -> this.yourTurn!!
                else -> true
            }
        }
}
