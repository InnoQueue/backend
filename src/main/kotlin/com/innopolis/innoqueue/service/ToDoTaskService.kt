package com.innopolis.innoqueue.service

import com.innopolis.innoqueue.dto.ToDoTaskDTO
import com.innopolis.innoqueue.model.UserQueue
import com.innopolis.innoqueue.repository.QueueRepository
import com.innopolis.innoqueue.repository.UserQueueRepository
import com.innopolis.innoqueue.utils.UsersQueueLogic
import org.springframework.stereotype.Service

@Service
class ToDoTaskService(
    private val userService: UserService,
    private val queueService: QueueService,
    private val userQueueRepository: UserQueueRepository,
    private val queueRepository: QueueRepository
) {
    fun getTasks(token: String): List<ToDoTaskDTO> {
        val user = userService.getUserByToken(token)
        val tasks = user.tasks
            .map { queue ->
                ToDoTaskDTO(
                    queueId = queue.id,
                    name = queue.name,
                    color = queue.color,
                    isImportant = queue.userQueues.firstOrNull { u -> u.user?.id == user.id }?.isImportant,
                    trackExpenses = queue.trackExpenses,
                    hashCode = queueService.getHashCode(queueService.transformQueueToDTO(queue, true, user.id!!))
                )
            }
            .toList()
        val (importantTasks, otherTasks) = tasks.partition { it.isImportant!! }
        return importantTasks.sortedBy { it.name!! } + otherTasks.sortedBy { it.name!! }
    }

    fun completeTask(token: String, taskId: Long, expenses: Int?) {
        val user = userService.getUserByToken(token)
        val queue = queueService.getUserQueueByQueueId(user, taskId)
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
                UsersQueueLogic.assignNextUser(queue, userQueueRepository, queueRepository)
            }
        }
    }

    fun skipTask(token: String, taskId: Long) {
        val user = userService.getUserByToken(token)
        val queue = queueService.getUserQueueByQueueId(user, taskId)
        // User can skip a task if it's his turn
        if (user.tasks.firstOrNull { task -> task.id == taskId } != null) {
            queue.skips = queue.skips?.plus(1)
            userQueueRepository.save(queue)
            //TODO notify about skip
            UsersQueueLogic.assignNextUser(queue, userQueueRepository, queueRepository)
        }
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
        //TODO notify about complete
    }
}