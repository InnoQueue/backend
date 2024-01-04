package com.innopolis.innoqueue.domain.notification.service.impl

import com.innopolis.innoqueue.domain.notification.dao.NotificationRepository
import com.innopolis.innoqueue.domain.notification.enums.NotificationType
import com.innopolis.innoqueue.domain.notification.service.NotificationSenderServiceAbstract
import com.innopolis.innoqueue.domain.notification.service.impl.dto.UserPreferencesProperties
import com.innopolis.innoqueue.domain.user.model.User
import com.innopolis.innoqueue.domain.user.service.UserService
import com.innopolis.innoqueue.domain.userqueue.dao.UserQueueRepository
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service

/**
 * Service for sending notification messages of type COMPLETE
 */
@Service
@EnableConfigurationProperties(UserPreferencesProperties::class)
class CompleteNotificationSenderServiceImpl(
    applicationEventPublisher: ApplicationEventPublisher,
    userService: UserService,
    userQueueRepository: UserQueueRepository,
    notificationRepository: NotificationRepository,
    userPreferencesProperties: UserPreferencesProperties
) : NotificationSenderServiceAbstract(
    applicationEventPublisher,
    userService,
    userQueueRepository,
    notificationRepository,
    userPreferencesProperties.obligatoryNotifications
) {
    override fun notificationType() = NotificationType.COMPLETED

    override fun User.subscribed() = this.completed ?: true
}
