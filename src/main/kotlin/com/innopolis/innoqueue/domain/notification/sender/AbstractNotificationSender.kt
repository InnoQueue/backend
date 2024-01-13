package com.innopolis.innoqueue.domain.notification.sender

import com.innopolis.innoqueue.domain.notification.dao.NotificationRepository
import com.innopolis.innoqueue.domain.notification.dto.NotificationMessageDto
import com.innopolis.innoqueue.domain.notification.enums.NotificationType
import com.innopolis.innoqueue.domain.notification.listener.SendNotificationEvent
import com.innopolis.innoqueue.domain.notification.model.Notification
import com.innopolis.innoqueue.domain.user.model.User
import com.innopolis.innoqueue.domain.user.service.UserService
import com.innopolis.innoqueue.domain.userqueue.dao.UserQueueRepository
import com.innopolis.innoqueue.domain.userqueue.model.UserQueue
import org.slf4j.LoggerFactory
import org.springframework.context.ApplicationEventPublisher
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.time.ZoneOffset

/**
 * Abstract Service for preparing and sending notification messages
 */
abstract class AbstractNotificationSender(
    private val applicationEventPublisher: ApplicationEventPublisher,
    private val userService: UserService,
    private val userQueueRepository: UserQueueRepository,
    private val notificationRepository: NotificationRepository,
    private val obligatoryNotifications: List<NotificationType>
) : NotificationSender {
    private val logger = LoggerFactory.getLogger(javaClass)

    abstract fun User.subscribed(): Boolean

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

    /**
     * Saves notification into the database and sends it via firebase
     */
    @Transactional
    override fun sendNotificationMessage(notificationMessageDto: NotificationMessageDto) {
        logger.info(
            "Sending notification: notificationType=${notificationType()}, queueId=${notificationMessageDto.queueId}"
        )
        val notifications = prepareNotificationsListToSend(notificationMessageDto)
        notificationRepository.saveAll(notifications)
        applicationEventPublisher.publishEvent(
            SendNotificationEvent(
                notificationType = notificationType(),
                notificationMessageDto = notificationMessageDto,
                notifications = notifications
            )
        )
    }

    private fun UserQueue.shouldSendMessage(participantId: Long): Boolean =
        if (notificationType() in obligatoryNotifications) true else
            userService
                // TODO n+1 problem
                .findUserById(this.userQueueId?.userId!!)!!
                .isUserSubscribed(participantId)

    private fun User.isUserSubscribed(participantId: Long): Boolean =
        if (this.id == participantId) true else this.subscribed()
}
