package com.innopolis.innoqueue.domain.queue.dao

import com.innopolis.innoqueue.domain.queue.model.Queue
import com.innopolis.innoqueue.domain.queue.model.QueueAndUserQueue
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

private const val GET_TODO_TASKS = """
SELECT queue.queue_id queueId,
       queue.name queueName,
       queue.color queueColor,
       queue.is_important isImportant,
       queue.track_expenses trackExpenses
FROM queue
         JOIN user_queue ON queue.queue_id = user_queue.queue_id
WHERE queue.current_user_id = user_queue.user_id
  AND queue.current_user_id = (SELECT user_id
                               FROM "user"
                               WHERE token = :token)
  AND queue.queue_id in (SELECT queue_id
                         FROM user_queue
                         GROUP BY queue_id
                         HAVING COUNT(*) > 1)
ORDER BY queue.name, queue.is_important DESC;  
"""

private const val GET_PIN_CODES = """
SELECT pin_code
FROM queue
WHERE pin_code IS NOT NULL;
"""

private const val GET_QR_CODES = """
SELECT qr_code
FROM queue
WHERE qr_code IS NOT NULL;
"""

/**
 * DAO repository for working with "queue" db table
 */
@Repository
interface QueueRepository : CrudRepository<Queue, Long>, JpaSpecificationExecutor<Queue> {
    /**
     * Returns queues for which a particular user is on duty
     * @param token - user token
     */
    @Query(GET_TODO_TASKS, nativeQuery = true)
    fun findToDoTasks(token: String): List<QueueAndUserQueue>

    /**
     * Returns list of existing pin codes
     */
    @Query(GET_PIN_CODES, nativeQuery = true)
    fun findPinCodes(): List<String>

    /**
     * Returns list of existing QR codes
     */
    @Query(GET_QR_CODES, nativeQuery = true)
    fun findQrCodes(): List<String>
}
