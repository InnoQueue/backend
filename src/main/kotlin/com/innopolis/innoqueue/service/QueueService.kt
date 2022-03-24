package com.innopolis.innoqueue.service

import com.innopolis.innoqueue.controller.dto.JoinQueueDTO
import com.innopolis.innoqueue.dto.*
import com.innopolis.innoqueue.model.Queue
import com.innopolis.innoqueue.model.User
import com.innopolis.innoqueue.model.UserQueue
import com.innopolis.innoqueue.repository.QueueRepository
import com.innopolis.innoqueue.repository.UserQueueRepository
import com.innopolis.innoqueue.utility.StringGenerator
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class QueueService(
    private val userService: UserService,
    private val userQueueRepository: UserQueueRepository,
    private val queueRepository: QueueRepository
) {
    fun getQueues(token: String): QueuesListDTO {
        val user = userService.getUserByToken(token)
        val (activeQueue, frozenQueue) = user.queues.partition { it.isActive!! }
        return QueuesListDTO(
            transformUserQueueToQueueDTO(activeQueue, true, user.id!!),
            transformUserQueueToQueueDTO(frozenQueue, false, user.id!!)
        )
    }

    fun createQueue(token: String, queue: NewQueueDTO): QueueDTO {
        val user = userService.getUserByToken(token)
        val createdQueue = saveQueueEntity(queue, user)
        saveUserQueueEntity(createdQueue, user)
        return QueueDTO(
            createdQueue.id!!,
            createdQueue.name!!,
            createdQueue.color!!,
            transformUserToUserExpensesDTO(createdQueue.currentUser, createdQueue),
            emptyList(),
            createdQueue.trackExpenses!!,
            isActive = true,
            isAdmin = true,
            createdQueue.link!!
        )
    }

    fun editQueue(token: String, editQueue: EditQueueDTO): QueueDTO {
        val user = userService.getUserByToken(token)
        val userQueue = getUserQueueByQueueId(user, editQueue.queueId)
        if (userQueue.queue?.creator?.id != user.id) {
            throw IllegalArgumentException("User is not an admin in this queue: ${editQueue.queueId}")
        }
        val queueEntity = queueRepository.findByIdOrNull(editQueue.queueId)
            ?: throw NoSuchElementException("Queue does not exist. ID: ${editQueue.queueId}")
        queueEntity.name = editQueue.name
        queueEntity.color = editQueue.color
        queueEntity.trackExpenses = editQueue.trackExpenses
        val updatedQueue = queueRepository.save(queueEntity)
        val userToDelete = userQueueRepository
            .findAll()
            .filter { it.queue?.id == updatedQueue.id }
            .filter { it.user?.id !in editQueue.participants }
            .filter { it.user?.id != user.id }
        userQueueRepository.deleteAll(userToDelete)

        return QueueDTO(
            updatedQueue.id!!,
            updatedQueue.name!!,
            updatedQueue.color!!,
            transformUserToUserExpensesDTO(updatedQueue.currentUser, updatedQueue),
            userQueueRepository
                .findAll()
                .filter { it.queue?.id == updatedQueue.id }
                .map { transformUserToUserExpensesDTO(it.user, updatedQueue) },
            updatedQueue.trackExpenses!!,
            updatedQueue.userQueues.firstOrNull { it.user?.id == user.id }?.isActive!!,
            true,
            updatedQueue.link!!
        )
    }

    fun getUserQueueByQueueId(user: User, queueId: Long): UserQueue {
        return user.queues.firstOrNull { task -> task.queue?.id == queueId }
            ?: throw IllegalArgumentException("User does not belong to such queue: $queueId")
    }

    fun freezeUnFreezeQueue(token: String, queueId: Long, status: Boolean) {
        val user = userService.getUserByToken(token)
        val userQueue = getUserQueueByQueueId(user, queueId)
        userQueue.isActive = status
        userQueueRepository.save(userQueue)
        //TODO notify about freezing
    }

    fun deleteQueue(token: String, queueId: Long) {
        val user = userService.getUserByToken(token)
        val userQueue = getUserQueueByQueueId(user, queueId)
        if (userQueue.queue?.creator?.id == user.id) {
            val participants = userQueue.queue?.userQueues!!
            userQueueRepository.deleteAll(participants)
            queueRepository.delete(userQueue.queue!!)
            //TODO notify about deletion
        } else {
            userQueueRepository.delete(userQueue)
            //TODO notify about leaving
        }
    }

    fun joinQueue(token: String, queue: JoinQueueDTO) {
        val user = userService.getUserByToken(token)
        val queueEntity = queueRepository.findAll().firstOrNull { it.link == queue.link }
            ?: throw IllegalArgumentException("The link for queue is invalid: ${queue.link}")
        if (user.queues.none { it.queue?.link == queue.link }) {
            val userQueue = createUserQueueEntity(user, queueEntity)
            userQueueRepository.save(userQueue)
            // TODO notify others that user joined
        }
    }

    private fun transformUserQueueToQueueDTO(userQueueList: List<UserQueue>, isActive: Boolean, userId: Long) =
        userQueueList.map { it.queue }
            .map { q ->
                QueueDTO(
                    queueId = q?.id!!,
                    queueName = q.name!!,
                    queueColor = q.color!!,
                    currentUser = transformUserToUserExpensesDTO(q.currentUser, q),
                    participants = q.userQueues
                        .filter { it.user?.id != userId && it.user?.id != q.currentUser?.id }
                        .map { userQueue -> userQueue.user }
                        .map { transformUserToUserExpensesDTO(it, q) },
                    trackExpenses = q.trackExpenses!!,
                    isActive = isActive,
                    isAdmin = q.creator?.id == userId,
                    link = q.link!!
                )
            }

    private fun transformUserToUserExpensesDTO(user: User?, queue: Queue): UserExpensesDTO = UserExpensesDTO(
        user?.id!!,
        user.name!!,
        queue.userQueues.firstOrNull { it.user?.id == user.id }?.expenses
    )

    private fun generateLink(): String {
        val queuesLinks = queueRepository.findAll().map { it.link }
        val generator = StringGenerator(16)
        while (true) {
            val randomString = generator.generateString()
            if (!queuesLinks.contains(randomString)) {
                return randomString
            }
        }
    }

    private fun saveQueueEntity(queue: NewQueueDTO, user: User): Queue {
        val queueEntity = Queue()
        queueEntity.name = queue.name
        queueEntity.color = queue.color
        queueEntity.creator = user
        queueEntity.trackExpenses = queue.trackExpenses
        queueEntity.link = generateLink()
        queueEntity.currentUser = user
        return queueRepository.save(queueEntity)
    }

    private fun saveUserQueueEntity(queue: Queue, user: User): UserQueue {
        val userQueueEntity = createUserQueueEntity(user, queue)
        return userQueueRepository.save(userQueueEntity)
    }

    private fun createUserQueueEntity(user: User, queue: Queue): UserQueue {
        val userQueue = UserQueue()
        userQueue.queue = queue
        userQueue.user = user
        userQueue.isActive = true
        userQueue.skips = 0
        userQueue.expenses = 0
        userQueue.isImportant = false
        userQueue.dateJoined = LocalDateTime.now()
        return userQueue
    }
}