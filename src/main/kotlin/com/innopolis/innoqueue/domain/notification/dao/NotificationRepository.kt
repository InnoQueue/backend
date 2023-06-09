package com.innopolis.innoqueue.domain.notification.dao

import com.innopolis.innoqueue.domain.notification.model.Notification
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

private const val GET_NOTIFICATIONS_QUERY = """
SELECT *
FROM notification
WHERE user_id = (SELECT user_id
                 FROM "user"
                 WHERE token = :token)
"""

private const val GET_NOTIFICATIONS_QUERY_SORTED = """
SELECT *
FROM notification
WHERE user_id = (SELECT user_id
                 FROM "user"
                 WHERE token = :token)
order by "date" DESC;
"""

private const val GET_NOTIFICATIONS_COUNT = """
SELECT count(*)
FROM notification
WHERE user_id = (SELECT user_id
                 FROM "user"
                 WHERE token = :token);
"""

private const val ANY_UNREAD_NOTIFICATION_QUERY = """
SELECT EXISTS(
               SELECT *
               FROM notification
               WHERE user_id = (SELECT user_id
                                FROM "user"
                                WHERE token = :token)
                 AND is_read = false
           );    
"""

private const val GET_EXPIRED_NOTIFICATIONS_QUERY = """
SELECT *
FROM notification
WHERE "date" < current_timestamp + INTERVAL '- 2 WEEK';   
"""

/**
 * DAO repository for working with "notification" db table
 */
@Repository
interface NotificationRepository : CrudRepository<Notification, Long> {
    /**
     * Returns all notification for a particular user token with pagination
     * @param token - user token
     */
    @Query(
        value = GET_NOTIFICATIONS_QUERY,
        countQuery = GET_NOTIFICATIONS_COUNT,
        nativeQuery = true
    )
    fun findAllByToken(token: String, pageable: Pageable): Page<Notification>

    /**
     * Returns all notification for a particular user token
     * @param token - user token
     */
    @Query(GET_NOTIFICATIONS_QUERY_SORTED, nativeQuery = true)
    fun findAllByToken(token: String): List<Notification>

    /**
     * Returns boolean whether there is any unread notification for a particular user token
     * @param token - user token
     */
    @Query(ANY_UNREAD_NOTIFICATION_QUERY, nativeQuery = true)
    fun anyUnreadNotification(token: String): Boolean

    /**
     * Returns notifications which are older than 2 weeks
     */
    @Query(GET_EXPIRED_NOTIFICATIONS_QUERY, nativeQuery = true)
    fun findAllExpiredNotifications(): List<Notification>
}
