package com.innopolis.innoqueue.dto

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * DTO for returning unread and read notification messages
 */
data class NotificationsListDTO(
    @JsonProperty("unread")
    val unreadNotifications: List<NotificationDTO>,
    @JsonProperty("all")
    val allNotifications: List<NotificationDTO>,
)
