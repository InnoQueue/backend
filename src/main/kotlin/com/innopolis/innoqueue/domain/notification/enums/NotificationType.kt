package com.innopolis.innoqueue.domain.notification.enums

/**
 * Notification messages type
 */
enum class NotificationType {
    YOUR_TURN,
    // TODO rename to present simple
    COMPLETED, SKIPPED, SHOOK, FROZEN, UNFROZEN, JOINED_QUEUE, LEFT_QUEUE,
    DELETE_QUEUE, UPDATE, OTHER
}
