package com.innopolis.innoqueue.service

import com.innopolis.innoqueue.dto.*
import com.innopolis.innoqueue.model.Queue
import com.innopolis.innoqueue.model.User
import com.innopolis.innoqueue.model.UserQueue
import com.innopolis.innoqueue.repository.QueueRepository
import com.innopolis.innoqueue.repository.UserQueueRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class QueueService(
    private val userService: UserService,
    private val userQueueRepository: UserQueueRepository,
    private val queueRepository: QueueRepository
) {
    fun getQueues(token: Long): QueuesListDTO {
        val user = userService.getUserByToken(token)
        val (activeQueue, frozenQueue) = user.queues.partition { it.isActive!! }
        return QueuesListDTO(
            transformUserQueueToQueueDTO(activeQueue, true, user.id!!),
            transformUserQueueToQueueDTO(frozenQueue, false, user.id!!)
        )
    }

    fun createQueue(token: Long, queue: NewQueueDTO): QueueDTO {
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
        while (true) {
            val length = 16
            val charPool: List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')
            val randomString = (1..length)
                .map { i -> kotlin.random.Random.nextInt(0, charPool.size) }
                .map(charPool::get)
                .joinToString("")
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
        val userQueueEntity = UserQueue()
        userQueueEntity.queue = queue
        userQueueEntity.user = user
        userQueueEntity.isActive = true
        userQueueEntity.skips = 0
        userQueueEntity.expenses = 0
        userQueueEntity.isImportant = false
        userQueueEntity.dateJoined = LocalDateTime.now()
        return userQueueRepository.save(userQueueEntity)
    }

    fun editQueue(token: Long, editQueue: EditQueueDTO): QueueDTO {
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

    fun freezeUnFreezeQueue(token: Long, queueId: Long, status: Boolean) {
        val user = userService.getUserByToken(token)
        val userQueue = getUserQueueByQueueId(user, queueId)
        userQueue.isActive = status
        userQueueRepository.save(userQueue)
    }

    fun deleteQueue(token: Long, queueId: Long) {
        val user = userService.getUserByToken(token)
        val userQueue = getUserQueueByQueueId(user, queueId)
        if (userQueue.queue?.creator?.id == user.id) {
            val participants = userQueue.queue?.userQueues!!
            userQueueRepository.deleteAll(participants)
            queueRepository.delete(userQueue.queue!!)
        } else {
            userQueueRepository.delete(userQueue)
        }
    }
}