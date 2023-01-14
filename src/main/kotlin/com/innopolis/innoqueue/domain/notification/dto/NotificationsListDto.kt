package com.innopolis.innoqueue.domain.notification.dto

/**
 * DTO for returning unread and read notification messages
 */
data class NotificationsListDto(
    val unreadNotifications: List<NotificationDto>,
    val allNotifications: List<NotificationDto>,
)
