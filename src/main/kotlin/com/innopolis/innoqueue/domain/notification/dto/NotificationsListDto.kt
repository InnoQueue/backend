package com.innopolis.innoqueue.domain.notification.dto

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * DTO for returning unread and read notification messages
 */
data class NotificationsListDto(
    @JsonProperty("unread")
    val unreadNotifications: List<NotificationDto>,
    @JsonProperty("all")
    val allNotifications: List<NotificationDto>,
)
