package com.innopolis.innoqueue.dao

import com.innopolis.innoqueue.models.Notification
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

private const val GET_NOTIFICATIONS_QUERY = """
SELECT *
FROM notifications
WHERE user_id = (SELECT user_id
                 FROM "user"
                 WHERE token = :token)
order by "date" DESC;
"""

private const val ANY_UNREAD_NOTIFICATION_QUERY = """
SELECT EXISTS(
               SELECT *
               FROM notifications
               WHERE user_id = (SELECT user_id
                                FROM "user"
                                WHERE token = :token)
                 AND is_read = false
           );    
"""

private const val GET_EXPIRED_NOTIFICATIONS_QUERY = """
SELECT *
FROM notifications
WHERE "date" < current_timestamp + INTERVAL '- 2 WEEK';   
"""

@Repository
interface NotificationRepository : CrudRepository<Notification, Long> {
    @Query(GET_NOTIFICATIONS_QUERY, nativeQuery = true)
    fun findAllByToken(token: String): List<Notification>

    @Query(ANY_UNREAD_NOTIFICATION_QUERY, nativeQuery = true)
    fun anyUnreadNotification(token: String): Boolean

    @Query(GET_EXPIRED_NOTIFICATIONS_QUERY, nativeQuery = true)
    fun findAllExpiredNotifications(): List<Notification>
}
