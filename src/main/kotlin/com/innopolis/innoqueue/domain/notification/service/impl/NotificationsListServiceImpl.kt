package com.innopolis.innoqueue.domain.notification.service.impl

import com.innopolis.innoqueue.domain.notification.dao.NotificationRepository
import com.innopolis.innoqueue.domain.notification.dto.NotificationDto
import com.innopolis.innoqueue.domain.notification.enums.NotificationType
import com.innopolis.innoqueue.domain.notification.model.Notification
import com.innopolis.innoqueue.domain.notification.service.NotificationsListService
import com.innopolis.innoqueue.domain.queue.dao.QueueRepository
import com.innopolis.innoqueue.domain.user.service.UserService
import com.innopolis.innoqueue.rest.v1.dto.EmptyDto
import com.innopolis.innoqueue.rest.v1.dto.NewNotificationDto
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

private const val DELETED_USER_NAME = "Deleted user"
private const val DELETED_QUEUE_NAME = "Deleted queue"
private const val CLEAR_RESPONSE = "Old notifications were deleted"

/**
 * Service for working with notification messages list
 */
@Service
class NotificationsListServiceImpl(
    private val userService: UserService,
    private val queueRepository: QueueRepository,
    private val notificationRepository: NotificationRepository
) : NotificationsListService {

    /**
     * Lists all notifications
     * @param token - user token
     */
    @Transactional
    override fun getNotifications(token: String, pageable: Pageable): Page<NotificationDto> = notificationRepository
        .findAllByToken(token, pageable)
        .toNotificationDTO()

    /**
     * Returns boolean whether there is any unread notification
     * @param token - user token
     */
    @Transactional
    override fun anyNewNotification(token: String): NewNotificationDto =
        NewNotificationDto(notificationRepository.anyUnreadNotification(token))

    /**
     * Marks notifications as read
     * @param token - user token
     */
    @Transactional
    override fun readNotifications(token: String, notificationIds: List<Long>?) {
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
    override fun clearOldNotifications(): EmptyDto {
        notificationRepository.deleteAll(notificationRepository.findAllExpiredNotifications())
        return EmptyDto(CLEAR_RESPONSE)
    }

    /**
     * Delete specified by id notifications
     */
    @Transactional
    override fun deleteNotifications(token: String, notificationIds: List<Long>?) {
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
    override fun deleteNotificationById(token: String, notificationId: Long) {
        notificationRepository
            .findAllByToken(token)
            .firstOrNull { it.id == notificationId }
            ?.let {
                notificationRepository.delete(it)
            }
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
}
