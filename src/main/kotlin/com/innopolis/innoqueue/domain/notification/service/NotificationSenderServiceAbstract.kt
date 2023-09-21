package com.innopolis.innoqueue.domain.notification.service

import com.innopolis.innoqueue.domain.fcmtoken.service.FcmTokenService
import com.innopolis.innoqueue.domain.notification.dao.NotificationRepository
import com.innopolis.innoqueue.domain.notification.dto.NotificationMessageDto
import com.innopolis.innoqueue.domain.notification.enums.NotificationType
import com.innopolis.innoqueue.domain.notification.model.Notification
import com.innopolis.innoqueue.domain.user.model.User
import com.innopolis.innoqueue.domain.user.service.UserService
import com.innopolis.innoqueue.domain.userqueue.dao.UserQueueRepository
import com.innopolis.innoqueue.domain.userqueue.model.UserQueue
import com.innopolis.innoqueue.webclients.firebase.service.FirebaseMessagingNotificationsService
import org.slf4j.LoggerFactory
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.time.ZoneOffset

/**
 * Service for preparing and sending notification messages
 */
abstract class NotificationSenderServiceAbstract(
    private val firebaseMessagingService: FirebaseMessagingNotificationsService,
    private val userService: UserService,
    private val fcmTokenService: FcmTokenService,
    private val userQueueRepository: UserQueueRepository,
    private val notificationRepository: NotificationRepository,
    private val obligatoryNotifications: List<NotificationType>
) : NotificationSenderService {
    private val logger = LoggerFactory.getLogger(javaClass)

    abstract fun notificationType(): NotificationType

    abstract fun User.subscribed(): Boolean

    /**
     * Saves notification in database and sends it via firebase
     */
    // TODO run quartz job
    @Transactional
    override fun sendNotificationMessage(notificationMessageDto: NotificationMessageDto) {
        logger.info(
            "Sending notification: notificationType=${notificationType()}, queueId=${notificationMessageDto.queueId}"
        )
        val notifications = prepareNotificationsListToSend(notificationMessageDto)
        notificationRepository.saveAll(notifications)
        firebaseMessagingService.sendNotificationsToFirebase(
            addressees = notifications
                .mapNotNull { it.user }
                .map { it.id!! to fcmTokenService.findTokensForUser(it.id!!) },
            notificationType = notificationType(),
            participant = notificationMessageDto.participantId to notificationMessageDto.participantName,
            queue = notificationMessageDto.queueId to notificationMessageDto.queueName,
        )
    }

    open fun prepareNotificationsListToSend(notificationMessageDto: NotificationMessageDto): List<Notification> =
        with(notificationMessageDto) {
            userQueueRepository.findUserQueueByQueueId(queueId)
                .filter { it.shouldSendMessage(participantId) }
                .map {
                    createNotification(
                        recipientUserId = it.userQueueId?.userId!!,
                        participantUserId = participantId,
                        referredQueueId = queueId
                    )
                }
        }

    fun createNotification(
        recipientUserId: Long,
        participantUserId: Long,
        referredQueueId: Long
    ) = Notification().apply {
        user = userService.findUserById(recipientUserId)
        participantId = participantUserId
        messageType = notificationType()
        queueId = referredQueueId
        isRead = false
        date = LocalDateTime.now(ZoneOffset.UTC)
    }

    private fun UserQueue.shouldSendMessage(participantId: Long): Boolean =
        if (notificationType() in obligatoryNotifications) true else
            userService
                .findUserById(this.userQueueId?.userId!!)!!
                .isUserSubscribed(participantId)

    private fun User.isUserSubscribed(participantId: Long): Boolean =
        if (this.id == participantId) true else this.subscribed()
}
