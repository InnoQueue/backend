package com.innopolis.innoqueue.dao

import com.innopolis.innoqueue.models.Queue
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

private const val GET_TODO_TASKS = """
SELECT queue.queue_id queueId,
       queue.name queueName,
       queue.color queueColor,
       user_queue.is_important isImportant,
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
ORDER BY queue.name, user_queue.is_important DESC;  
"""

@Repository
interface QueueRepository : CrudRepository<Queue, Long> {
    @Query(GET_TODO_TASKS, nativeQuery = true)
    fun findToDoTasks(token: String): List<QueueAndUserQueue>
}
