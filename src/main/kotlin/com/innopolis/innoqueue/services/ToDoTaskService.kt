package com.innopolis.innoqueue.services

import com.innopolis.innoqueue.dao.QueueRepository
import com.innopolis.innoqueue.dao.UserQueueRepository
import com.innopolis.innoqueue.dto.ToDoTaskDTO
import com.innopolis.innoqueue.enums.NotificationsType
import com.innopolis.innoqueue.models.UserQueue
import com.innopolis.innoqueue.utils.UsersQueueLogic
import org.hibernate.Hibernate
import org.springframework.stereotype.Service
import java.util.*

@Service
class ToDoTaskService(
    private val userService: UserService,
    private val queueService: QueueService,
    private val notificationsService: NotificationsService,
    private val queueRepository: QueueRepository,
    private val userQueueRepository: UserQueueRepository,
) {
    fun getToDoTasks(token: String): List<ToDoTaskDTO> = queueRepository
        .findToDoTasks(token)
        .map {
            ToDoTaskDTO(
                queueId = it.getQueueId(),
                name = it.getQueueName(),
                color = it.getQueueColor(),
                isImportant = it.getIsImportant(),
                trackExpenses = it.getTrackExpenses(),
                hashCode = queueService.getHashCode(it.getQueueId())
            )
        }

    fun completeTask(token: String, taskId: Long, expenses: Double?) {
        val userQueue = userQueueRepository.findUserQueue(token, taskId)
        // If this queue requires to track expenses it should not be null or negative number
        if (userQueue.getTrackExpenses() && (expenses == null || expenses < 0)) {
            throw IllegalArgumentException("Expenses should be a non negative number")
        }
        // TODO validate if participants > 1
        // TODO rewrite the logic
//        addProgress(queue, expenses)
//        // if it was user's turn in this queue, and he didn't have skips then the turn is assigned to the next user
//        if (userQueue.getUserId() == userQueue.getCurrentUserId() && userQueue.getSkips() <= 0) {
//            // Assign the next user in a queue
//            val nextUser = UsersQueueLogic.assignNextUser(queue, userQueueRepository, queueRepository)
//            notificationsService.sendNotificationMessage(
//                NotificationsType.YOUR_TURN,
//                nextUser,
//                queue.queue!!
//            )
//        }

        userQueueRepository.findUserQueueByToken(token, taskId)?.let {
            // If user is not next in this queue
            if (userQueue.getUserId() != userQueue.getCurrentUserId()) {
                addProgress(it, expenses)
            } // if it's user's turn in this queue
            else {
                // if user completed a task, but he had skips, then he still is the next one of this queue
                if (userQueue.getSkips() > 0) {
                    addProgress(it, expenses)
                }// if user completed a task and didn't have skips then the turn is assigned to the next user
                else {
                    saveTaskProgress(it, expenses)
                    // Assign the next user in a queue
                    val nextUser = UsersQueueLogic.assignNextUser(it, userQueueRepository, queueRepository)
                    notificationsService.sendNotificationMessage(
                        NotificationsType.YOUR_TURN,
                        nextUser.id!!,
                        nextUser.name!!,
                        it.queue?.id!!,
                        it.queue?.name!!
                    )
                }
            }
        }
    }

    fun skipTask(token: String, taskId: Long) {
        val userQueue = userQueueRepository.findUserQueue(token, taskId)
        // User can skip a task if it's his turn
        if (userQueue.getUserId() == userQueue.getCurrentUserId()) {
            val user = userService.findUserByToken(token)
            val queue = queueService.getUserQueueByQueueId(user, taskId)
            queue.skips = queue.skips?.plus(1)
            userQueueRepository.save(queue)
            notificationsService.sendNotificationMessage(
                NotificationsType.SKIPPED,
                user.id!!,
                user.name!!,
                queue.queue?.id!!,
                queue.queue?.name!!
            )
            val nextUser = UsersQueueLogic.assignNextUser(queue, userQueueRepository, queueRepository)
            notificationsService.sendNotificationMessage(
                NotificationsType.YOUR_TURN,
                nextUser.id!!,
                nextUser.name!!,
                queue.queue?.id!!,
                queue.queue?.name!!
            )
        }
    }

    private fun addProgress(queue: UserQueue, expenses: Double?) {
        queue.skips = queue.skips?.minus(1)
        saveTaskProgress(queue, expenses)
    }

    private fun saveTaskProgress(queue: UserQueue, expenses: Double?) {
        if (expenses != null && queue.queue?.trackExpenses == true) {
            val roundedExpenses = String.format(Locale.ENGLISH, "%.2f", expenses).toDouble()
            queue.expenses = queue.expenses?.plus(roundedExpenses)
        }
        queue.isImportant = false
        val savedQueue = userQueueRepository.save(queue)
        Hibernate.initialize(queue.user)
        Hibernate.initialize(savedQueue.queue)
        notificationsService.sendNotificationMessage(
            NotificationsType.COMPLETED,
            queue.user?.id!!,
            queue.user?.name!!,
            savedQueue.queue?.id!!,
            savedQueue.queue?.name!!
        )
    }
}
