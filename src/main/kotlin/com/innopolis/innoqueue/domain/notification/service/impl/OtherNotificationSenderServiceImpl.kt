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
 * Service for sending notification messages of type OTHER
 */
@Service
@EnableConfigurationProperties(UserPreferencesProperties::class)
class OtherNotificationSenderServiceImpl(
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
    override fun notificationType() = NotificationType.OTHER

    // TODO add column to DB
    override fun User.subscribed() = true
}
