package com.innopolis.innoqueue.service

import com.innopolis.innoqueue.dto.NewQueueDTO
import com.innopolis.innoqueue.dto.QueueDTO
import com.innopolis.innoqueue.dto.QueuesListDTO
import com.innopolis.innoqueue.dto.UserExpensesDTO
import com.innopolis.innoqueue.model.Queue
import com.innopolis.innoqueue.model.User
import com.innopolis.innoqueue.model.UserQueue
import com.innopolis.innoqueue.repository.QueueRepository
import com.innopolis.innoqueue.repository.UserQueueRepository
import com.innopolis.innoqueue.repository.UserRepository
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class QueueService(
    private val userRepository: UserRepository,
    private val userQueueRepository: UserQueueRepository,
    private val queueRepository: QueueRepository
) {
    fun getQueues(token: Long): QueuesListDTO {
        val user = userRepository.findAll().firstOrNull { user -> user.token == token }
            ?: throw java.util.NoSuchElementException("No such user with token: $token")
        val (activeQueue, frozenQueue) = user.queues.partition { it.isActive!! }
        return QueuesListDTO(
            transformUserQueueToQueueDTO(activeQueue, true, user.id!!),
            transformUserQueueToQueueDTO(frozenQueue, false, user.id!!)
        )
    }

    fun createQueue(token: Long, queue: NewQueueDTO): QueueDTO {
        val user = userRepository.findAll().firstOrNull { user -> user.token == token }
            ?: throw java.util.NoSuchElementException("No such user with token: $token")
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
        user?.name!!,
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
}