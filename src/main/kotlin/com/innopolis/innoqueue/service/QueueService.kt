package com.innopolis.innoqueue.service

import com.innopolis.innoqueue.controller.dto.JoinQueueDTO
import com.innopolis.innoqueue.dto.*
import com.innopolis.innoqueue.model.Queue
import com.innopolis.innoqueue.model.User
import com.innopolis.innoqueue.model.UserQueue
import com.innopolis.innoqueue.repository.QueueRepository
import com.innopolis.innoqueue.repository.UserQueueRepository
import com.innopolis.innoqueue.utility.StringGenerator
import com.innopolis.innoqueue.utility.UsersQueueLogic
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
        val act = transformUserQueueToQueueDTO(activeQueue, true, user.id!!).sortedBy { it.queueName }
        val froz = transformUserQueueToQueueDTO(frozenQueue, false, user.id!!).sortedBy { it.queueName }
        println("LOG active queus $act")
        println("LOG frozem queus $froz")
        return QueuesListDTO(
            act, froz

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
            isYourTurn = true,
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
            isYourTurn = updatedQueue.currentUser?.id == user.id,
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
        when (status) {
            true -> {
                userQueue.isActive = true
                userQueueRepository.save(userQueue)
                //TODO notify about unfreezing
            }
            false -> {
                // You can't freeze queue if it's your turn
                if (userQueue.queue?.currentUser?.id != user.id) {
                    userQueue.isActive = false
                    userQueueRepository.save(userQueue)
                    //TODO notify about freezing
                }
            }
        }
    }

    fun deleteQueue(token: String, queueId: Long) {
        val user = userService.getUserByToken(token)
        val userQueue = getUserQueueByQueueId(user, queueId)
        // Delete queue
        if (userQueue.queue?.creator?.id == user.id) {
            println("LOG Delete queue")
            val participants = userQueue.queue?.userQueues!!
            println("LOG participants: $participants")
            userQueueRepository.deleteAll(participants)
            println("LOG participants deleted")
            queueRepository.delete(userQueue.queue!!)
            println("LOG queue deleted")
            //TODO notify about deletion
        } // Leave queue
        else {
            userQueueRepository.delete(userQueue)
            //TODO notify about leaving
            // If it's your turn, reassign another user
            if (userQueue.queue?.currentUser?.id == user.id) {
                UsersQueueLogic.assignNextUser(userQueue, userQueueRepository, queueRepository)
            }
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

    fun shakeUser(token: String, queueId: Long) {
        val user = userService.getUserByToken(token)
        val userQueue = getUserQueueByQueueId(user, queueId)
        if (userQueue.queue?.currentUser?.id == user.id) {
            throw IllegalArgumentException("You can't shake yourself!")
        }
        // TODO notification, shake user
    }

    private fun transformUserQueueToQueueDTO(
        userQueueList: List<UserQueue>,
        isActive: Boolean,
        userId: Long
    ): List<QueueDTO> = userQueueList.map { it.queue }.map { q ->
        QueueDTO(
            queueId = q?.id!!,
            queueName = q.name!!,
            queueColor = q.color!!,
            currentUser = transformUserToUserExpensesDTO(q.currentUser, q),
            isYourTurn = q.currentUser?.id == userId,
            participants = sortUserExpensesDTOByFrozen(q.userQueues
                .filter { it.user?.id != userId && it.user?.id != q.currentUser?.id }
                .map { userQueue -> userQueue.user }
                .map { transformUserToUserExpensesDTO(it, q) }),
            trackExpenses = q.trackExpenses!!,
            isActive = isActive,
            isAdmin = q.creator?.id == userId,
            link = q.link!!
        )
    }

    private fun sortUserExpensesDTOByFrozen(users: List<UserExpensesDTO>): List<UserExpensesDTO> {
        val (activeUsers, frozenUsers) = users.partition { it.isActive }
        return activeUsers.sortedBy { it.userName } + frozenUsers.sortedBy { it.userName }
    }

    private fun transformUserToUserExpensesDTO(user: User?, queue: Queue): UserExpensesDTO = UserExpensesDTO(
        user?.id!!,
        user.name!!,
        queue.userQueues.firstOrNull { it.user?.id == user.id }?.expenses,
        user.queues.firstOrNull { it.queue?.id == queue.id }?.isActive!!
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