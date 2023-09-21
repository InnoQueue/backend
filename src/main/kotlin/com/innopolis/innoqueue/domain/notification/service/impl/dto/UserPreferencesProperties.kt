package com.innopolis.innoqueue.domain.notification.service.impl.dto

import com.innopolis.innoqueue.domain.notification.enums.NotificationType
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConstructorBinding
@ConfigurationProperties("user-preferences")
data class UserPreferencesProperties(
    val obligatoryNotifications: List<NotificationType>
)
