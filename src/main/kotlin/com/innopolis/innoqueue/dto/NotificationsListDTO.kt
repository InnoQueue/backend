package com.innopolis.innoqueue.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class NotificationsListDTO(
    @JsonProperty("unread")
    val unreadNotifications: List<NotificationDTO>,
    @JsonProperty("all")
    val allNotifications: List<NotificationDTO>,
)
