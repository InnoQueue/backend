package com.innopolis.innoqueue.domain.notification.sender.impl

import com.innopolis.innoqueue.domain.notification.dao.NotificationRepository
import com.innopolis.innoqueue.domain.notification.enums.NotificationType
import com.innopolis.innoqueue.domain.notification.sender.AbstractNotificationSender
import com.innopolis.innoqueue.domain.notification.sender.property.UserPreferencesProperties
import com.innopolis.innoqueue.domain.user.model.User
import com.innopolis.innoqueue.domain.user.service.UserService
import com.innopolis.innoqueue.domain.userqueue.dao.UserQueueRepository
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Component

/**
 * Component for sending notification messages of type LEAVE_QUEUE
 */
@Component
@EnableConfigurationProperties(UserPreferencesProperties::class)
class LeaveQueueNotificationSender(
    applicationEventPublisher: ApplicationEventPublisher,
    userService: UserService,
    userQueueRepository: UserQueueRepository,
    notificationRepository: NotificationRepository,
    userPreferencesProperties: UserPreferencesProperties
) : AbstractNotificationSender(
    applicationEventPublisher,
    userService,
    userQueueRepository,
    notificationRepository,
    userPreferencesProperties.obligatoryNotifications
) {
    override fun notificationType() = NotificationType.LEFT_QUEUE

    override fun User.subscribed() = this.leftQueue ?: true
}
