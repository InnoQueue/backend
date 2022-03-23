package com.innopolis.innoqueue.service

import com.innopolis.innoqueue.dto.ToDoTaskDTO
import com.innopolis.innoqueue.model.User
import com.innopolis.innoqueue.model.UserQueue
import com.innopolis.innoqueue.repository.QueueRepository
import com.innopolis.innoqueue.repository.UserQueueRepository
import com.innopolis.innoqueue.repository.UserRepository
import org.springframework.stereotype.Service

@Service
class ToDoTaskService(
    private val userRepository: UserRepository,
    private val userQueueRepository: UserQueueRepository,
    private val queueRepository: QueueRepository
) {
    fun getTasks(token: Long): List<ToDoTaskDTO> {
        val user = userRepository.findAll().firstOrNull { user -> user.token == token }
            ?: throw java.util.NoSuchElementException("No such user with token: $token")
        return user.tasks
            .map { queue ->
                ToDoTaskDTO(
                    queue.id,
                    queue.name,
                    queue.color,
                    queue.userQueues.firstOrNull { u -> u.user?.id == user.id }?.isImportant,
                    queue.trackExpenses
                )
            }
            .toList()
    }

    fun completeTask(token: Long, taskId: Long, expenses: Int?) {
        val user = userRepository.findAll().firstOrNull { user -> user.token == token }
            ?: throw NoSuchElementException("No such user with token: $token")
        val queue = user.queues.firstOrNull { task -> task.queue?.id == taskId }
            ?: throw IllegalArgumentException("User does not belong to such queue: $taskId")
        // If user is not next in this queue
        if (user.tasks.none { task -> task.id == taskId }) {
            addProgress(queue, expenses)
        } // if it's user's turn in this queue
        else {
            // if user completed a task, but he had skips, then he still is the next one of this queue
            if (queue.skips!! > 0) {
                addProgress(queue, expenses)
            }// if user completed a task and didn't have skips then the turn is assigned to the next one user.
            else {
                saveTaskProgress(queue, expenses)
                // Assign the next user in a queue
                assignNextUser(queue)
            }
        }
    }

    fun skipTask(token: Long, taskId: Long) {
        val user = userRepository.findAll().firstOrNull { user -> user.token == token }
            ?: throw NoSuchElementException("No such user with token: $token")
        val queue = user.queues.firstOrNull { task -> task.queue?.id == taskId }
            ?: throw IllegalArgumentException("User does not belong to such queue: $taskId")
        // User can skip a task if it's his turn
        if (user.tasks.firstOrNull { task -> task.id == taskId } != null) {
            queue.skips = queue.skips?.plus(1)
            userQueueRepository.save(queue)
            assignNextUser(queue)
        }
    }

    private fun getUsersInQueue(queue: UserQueue): Pair<List<User?>, Int?> {
        val usersInQueue = userQueueRepository.findAll()
            .filter { it.queue?.id == queue.queue?.id }
            .sortedBy { it.dateJoined }
            .map { it.user }

        val currentUserIndex = usersInQueue
            .zip(usersInQueue.indices)
            .firstOrNull { (u, _) -> u?.id == queue.queue?.currentUser?.id }?.second

        return usersInQueue to currentUserIndex
    }

    private fun addProgress(queue: UserQueue, expenses: Int?) {
        queue.skips = queue.skips?.minus(1)
        saveTaskProgress(queue, expenses)
    }

    private fun saveTaskProgress(queue: UserQueue, expenses: Int?) {
        if (expenses != null) {
            queue.expenses = queue.expenses?.plus(expenses)
        }
        queue.isImportant = false
        userQueueRepository.save(queue)
    }

    private fun assignNextUser(queue: UserQueue) {
        val (usersInQueue, currentUserIndex) = getUsersInQueue(queue)
        var index: Int = currentUserIndex!! + 1
        while (true) {
            if (index >= usersInQueue.size) {
                index = 0
            }
            val nextUser = usersInQueue[index]
            val nextUserQueue = nextUser?.queues?.firstOrNull { q -> q.queue?.id == queue.queue?.id }!!

            // If next user has skips < 0, then increment his skip and assign a queue to the user after him
            if (nextUserQueue.skips!! < 0) {
                nextUserQueue.skips = nextUserQueue.skips!! + 1
                userQueueRepository.save(nextUserQueue)
                index++
            } // We found the next candidate
            else {
                val queueToUpdate = queueRepository.findAll().firstOrNull { it.id == queue.queue?.id }
                if (queueToUpdate != null) {
                    queueToUpdate.currentUser = nextUser
                    queueRepository.save(queueToUpdate)
                    break
                }
            }


        }
    }
}