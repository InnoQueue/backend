package com.innopolis.innoqueue.domain.queue.service.impl

import com.innopolis.innoqueue.domain.notification.dto.NotificationMessageDto
import com.innopolis.innoqueue.domain.notification.enums.NotificationType
import com.innopolis.innoqueue.domain.notification.service.NotificationSenderService
import com.innopolis.innoqueue.domain.queue.dao.QueueRepository
import com.innopolis.innoqueue.domain.queue.dto.ToDoTaskDto
import com.innopolis.innoqueue.domain.queue.service.QueueService
import com.innopolis.innoqueue.domain.queue.service.ToDoTaskService
import com.innopolis.innoqueue.domain.queue.util.UsersQueueLogic
import com.innopolis.innoqueue.domain.user.service.UserService
import com.innopolis.innoqueue.domain.userqueue.dao.UserQueueRepository
import com.innopolis.innoqueue.domain.userqueue.model.UserQueue
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * Service for working with queues for which user is on duty
 */
@Suppress("LongParameterList")
@Service
class ToDoTaskServiceImpl(
    private val userService: UserService,
    private val queueService: QueueService,
    private val notificationSenderService: NotificationSenderService,
    private val queueRepository: QueueRepository,
    private val userQueueRepository: UserQueueRepository,
) : ToDoTaskService {
    /**
     * Lists user queues for which he is responsible right now
     * @param token - user token
     */
    @Transactional
    override fun getToDoTasks(token: String): List<ToDoTaskDto> = queueRepository
        .findToDoTasks(token)
        .map {
            ToDoTaskDto(
                queueId = it.getQueueId(),
                queueName = it.getQueueName(),
                queueColor = it.getQueueColor(),
                important = it.getIsImportant(),
                trackExpenses = it.getTrackExpenses()
            )
        }

    /**
     * Add progress for a particular queue
     * @param token - user token
     * @param taskId - id of a queue
     */
    @Transactional
    override fun completeTask(token: String, taskId: Long, expenses: Long?) {
        val userQueue = userQueueRepository.findUserQueue(token, taskId)
        // If this queue requires to track expenses it should not be null or negative number
        if (userQueue.getTrackExpenses()) {
            require(expenses != null && expenses >= 0) { "Expenses should be a non negative number" }
        }
        // TODO validate if active participants > 1
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
                if (userQueue.getProgress() > 0) {
                    addProgress(it, expenses)
                }// if user completed a task and didn't have skips then the turn is assigned to the next user
                else {
                    saveTaskProgress(it, expenses)
                    // Assign the next user in a queue
                    val nextUser = UsersQueueLogic.assignNextUser(it, userService, userQueueRepository, queueRepository)
                    val queue = queueRepository.findAll().firstOrNull { q -> q.queueId == it.userQueueId?.queueId }!!
                    notificationSenderService.sendNotificationMessage(
                        NotificationType.YOUR_TURN,
                        NotificationMessageDto(
                            participantId = nextUser.id!!,
                            participantName = nextUser.name!!,
                            queueId = queue.queueId!!,
                            queueName = queue.name!!
                        )
                    )
                }
            }
        }
    }

    /**
     * Skip to-do for which user is responsible right now
     * @param token - user token
     * @param taskId - id of a queue
     */
    @Transactional
    override fun skipTask(token: String, taskId: Long) {
        val userQueueAndQueue = userQueueRepository.findUserQueue(token, taskId)
        // User can skip a task if it's his turn
        if (userQueueAndQueue.getUserId() == userQueueAndQueue.getCurrentUserId()) {
            val user = userService.findUserByToken(token)
            val userQueue = queueService.getUserQueueByQueueId(user, taskId)
            userQueue.progress = userQueue.progress?.plus(1)
            userQueue.skips = userQueue.skips?.plus(1)
            userQueueRepository.save(userQueue)
            val queue = queueRepository.findAll().firstOrNull { it.queueId == userQueue.userQueueId?.queueId }!!
            notificationSenderService.sendNotificationMessage(
                NotificationType.SKIPPED,
                NotificationMessageDto(
                    participantId = user.id!!,
                    participantName = user.name!!,
                    queueId = queue.queueId!!,
                    queueName = queue.name!!
                )
            )
            val nextUser = UsersQueueLogic.assignNextUser(userQueue, userService, userQueueRepository, queueRepository)
            notificationSenderService.sendNotificationMessage(
                NotificationType.YOUR_TURN,
                NotificationMessageDto(
                    participantId = nextUser.id!!,
                    participantName = nextUser.name!!,
                    queueId = queue.queueId!!,
                    queueName = queue.name!!
                )
            )
        }
    }

    private fun addProgress(queue: UserQueue, expenses: Long?) {
        queue.progress = queue.progress?.minus(1)
        saveTaskProgress(queue, expenses)
    }

    private fun saveTaskProgress(userQueue: UserQueue, expenses: Long?) {
        userQueue.completes = userQueue.completes?.plus(1)
        val queue = queueRepository.findAll().firstOrNull { it.queueId == userQueue.userQueueId?.queueId }!!
        if (expenses != null && queue.trackExpenses == true) {
            userQueue.expenses = userQueue.expenses?.plus(expenses)
        }
        queue.isImportant = false
        queueRepository.save(queue)
        userQueueRepository.save(userQueue)
        notificationSenderService.sendNotificationMessage(
            NotificationType.COMPLETED,
            NotificationMessageDto(
                participantId = userQueue.userQueueId?.userId!!,
                participantName = userService.findUserNameById(userQueue.userQueueId?.userId!!)!!,
                queueId = queue.queueId!!,
                queueName = queue.name!!
        )
        )
    }
}
