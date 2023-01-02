package com.innopolis.innoqueue.dao

import com.innopolis.innoqueue.model.UserQueue
import com.innopolis.innoqueue.model.UserQueueId
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

private const val GET_USER_QUEUE = """
SELECT user_queue.user_id    userId,
       queue.queue_id        queueId,
       queue.current_user_id currentUserId,
       queue.track_expenses  trackExpenses,
       user_queue.is_active  isActive,
       user_queue.progress,
       user_queue.completes,
       user_queue.skips,
       user_queue.expenses
FROM user_queue
         JOIN queue ON user_queue.queue_id = queue.queue_id
WHERE user_id = (SELECT user_id
                 FROM "user"
                 WHERE token = :token)
  AND user_queue.queue_id = :queueId ;
"""

private const val GET_USER_QUEUE_BY_TOKEN = """
SELECT *
FROM user_queue
WHERE user_id = (SELECT user_id
                 FROM "user"
                 WHERE token = :token)
  AND queue_id = :queueId ;
"""

private const val GET_USER_QUEUE_BY_QUEUE_ID = """
SELECT *
FROM user_queue
WHERE user_queue.queue_id = :queueId ;
"""

private const val GET_ALL_USER_QUEUE_BY_TOKEN = """
SELECT user_queue.queue_id queueId,
       queue.name queueName,
       queue.color,
       user_queue.is_active isActive
FROM user_queue
         JOIN queue ON user_queue.queue_id = queue.queue_id
WHERE user_id = (SELECT user_id
                 FROM "user"
                 WHERE token = :token)
ORDER BY queue.name;
"""

private const val GET_QUEUE_PARTICIPANTS = """
SELECT
       user_queue.user_id userId,
       "user".name userName,
       user_queue.expenses,
       user_queue.is_active isActive
FROM user_queue
         JOIN "user" ON user_queue.user_id = "user".user_id
WHERE user_queue.queue_id = :queueId ;
"""

/**
 * DAO repository for working with "user_queue" db table
 */
@Repository
interface UserQueueRepository : CrudRepository<UserQueue, UserQueueId> {
    /**
     * Returns user_queue model with custom fields for a particular user token and queueId
     * @param token - user token
     */
    @Query(GET_USER_QUEUE, nativeQuery = true)
    fun findUserQueue(token: String, queueId: Long): UserQueueAndQueue

    /**
     * Returns user_queue model for a particular user token and queueId
     * @param token - user token
     * @param queueId - id of queue
     */
    @Query(GET_USER_QUEUE_BY_TOKEN, nativeQuery = true)
    fun findUserQueueByToken(token: String, queueId: Long): UserQueue?

    /**
     * Returns a list of user_queue models for a particular queueId
     * @param queueId - id of queue
     */
    @Query(GET_USER_QUEUE_BY_QUEUE_ID, nativeQuery = true)
    fun findUserQueueByQueueId(queueId: Long): List<UserQueue>

    /**
     * Returns a list of user_queue models for a particular user token
     * @param token - user token
     */
    @Query(GET_ALL_USER_QUEUE_BY_TOKEN, nativeQuery = true)
    fun findAllUserQueueByToken(token: String): List<UserQueuesShortForm>

    /**
     * Returns a list of users participating in a particular queue
     * @param queueId - id of queue
     */
    @Query(GET_QUEUE_PARTICIPANTS, nativeQuery = true)
    fun findQueueParticipants(queueId: Long): List<QueueParticipants>
}
