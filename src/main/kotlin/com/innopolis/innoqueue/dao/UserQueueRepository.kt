package com.innopolis.innoqueue.dao

import com.innopolis.innoqueue.models.UserQueue
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

private const val GET_USER_QUEUE = """
SELECT user_queue.user_id    userId,
       queue.queue_id        queueId,
       queue.current_user_id currentUserId,
       queue.track_expenses  trackExpenses,
       user_queue.is_active  isActive,
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

@Repository
interface UserQueueRepository : CrudRepository<UserQueue, Long> {
    @Query(GET_USER_QUEUE, nativeQuery = true)
    fun findUserQueue(token: String, queueId: Long): UserQueueAndQueue

    @Query(GET_USER_QUEUE_BY_TOKEN, nativeQuery = true)
    fun findUserQueueByToken(token: String, queueId: Long): UserQueue?

    @Query(GET_USER_QUEUE_BY_QUEUE_ID, nativeQuery = true)
    fun findUserQueueByQueueId(queueId: Long): List<UserQueue>

    @Query(GET_ALL_USER_QUEUE_BY_TOKEN, nativeQuery = true)
    fun findAllUserQueueByToken(token: String): List<UserQueuesShortForm>

    @Query(GET_QUEUE_PARTICIPANTS, nativeQuery = true)
    fun findQueueParticipants(queueId: Long): List<QueueParticipants>
}
