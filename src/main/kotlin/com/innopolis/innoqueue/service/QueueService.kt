package com.innopolis.innoqueue.service

import com.innopolis.innoqueue.controller.dto.QueuePinCodeDTO
import com.innopolis.innoqueue.dto.*
import com.innopolis.innoqueue.model.Queue
import com.innopolis.innoqueue.model.QueuePinCode
import com.innopolis.innoqueue.model.User
import com.innopolis.innoqueue.model.UserQueue
import com.innopolis.innoqueue.repository.QueuePinCodeRepository
import com.innopolis.innoqueue.repository.QueueRepository
import com.innopolis.innoqueue.repository.UserQueueRepository
import com.innopolis.innoqueue.utility.StringGenerator
import com.innopolis.innoqueue.utility.UsersQueueLogic
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import kotlin.concurrent.thread

@Service
class QueueService(
    private val userService: UserService,
    private val userQueueRepository: UserQueueRepository,
    private val queueRepository: QueueRepository,
    private val queuePinCodeRepository: QueuePinCodeRepository
) {
    fun getQueues(token: String): QueuesListDTO {
        val user = userService.getUserByToken(token)
        val (activeQueue, frozenQueue) = user.queues.partition { it.isActive!! }
        return QueuesListDTO(
            transformUserQueueListToQueueShortDTO(activeQueue).sortedBy { it.queueName },
            transformUserQueueListToQueueShortDTO(frozenQueue).sortedBy { it.queueName }
        )
    }

    fun getQueueById(token: String, queueId: Long): QueueDTO {
        val user = userService.getUserByToken(token)
        val userQueue: UserQueue = getUserQueueByQueueId(user, queueId)
        return transformQueueToDTO(queue = userQueue.queue, isActive = userQueue.isActive!!, userId = user.id!!)
    }

    fun getQueuePinCode(token: String, queueId: Long): QueuePinCodeDTO {
        val user = userService.getUserByToken(token)
        val userQueue: UserQueue = getUserQueueByQueueId(user, queueId)
        val queuePinCodes = queuePinCodeRepository.findAll()
        val queuePinCode = queuePinCodes.firstOrNull { it.queue?.id == queueId }
        return if (queuePinCode == null) {
            val pinCode = generateUniquePinCode(queuePinCodes.map { it.pinCode })
            val newQueuePinCode = QueuePinCode()
            newQueuePinCode.queue = userQueue.queue
            newQueuePinCode.pinCode = pinCode
            val createdQueuePinCode = queuePinCodeRepository.save(newQueuePinCode)
            thread(start = true) {
                Thread.sleep(120_000)
                queuePinCodeRepository.delete(createdQueuePinCode)
            }
            QueuePinCodeDTO(pinCode)
        } else QueuePinCodeDTO(queuePinCode.pinCode!!)
    }

    private fun generateUniquePinCode(pinCodes: List<String?>): String {
        while (true) {
            val newPinCode = (1..4)
                .map { (0..9).random() }
                .fold("") { acc: String, i: Int -> acc + i.toString() }
            if (newPinCode !in pinCodes) {
                return newPinCode
            }
        }
    }

    fun createQueue(token: String, queue: NewQueueDTO): QueueDTO {
        val user = userService.getUserByToken(token)
        val createdQueue = saveQueueEntity(queue, user)
        saveUserQueueEntity(createdQueue, user)
        return QueueDTO(
            queueId = createdQueue.id!!,
            queueName = createdQueue.name!!,
            queueColor = createdQueue.color!!,
            currentUser = transformUserToUserExpensesDTO(createdQueue.currentUser, createdQueue),
            isYourTurn = true,
            participants = emptyList(),
            trackExpenses = createdQueue.trackExpenses!!,
            isActive = true,
            isAdmin = true,
            link = createdQueue.link!!
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
            queueId = updatedQueue.id!!,
            queueName = updatedQueue.name!!,
            queueColor = updatedQueue.color!!,
            currentUser = transformUserToUserExpensesDTO(updatedQueue.currentUser, updatedQueue),
            isYourTurn = updatedQueue.currentUser?.id == user.id,
            participants = userQueueRepository
                .findAll()
                .filter { it.queue?.id == updatedQueue.id }
                .map { transformUserToUserExpensesDTO(it.user, updatedQueue) },
            trackExpenses = updatedQueue.trackExpenses!!,
            isActive = updatedQueue.userQueues.firstOrNull { it.user?.id == user.id }?.isActive!!,
            isAdmin = true,
            link = updatedQueue.link!!
        )
    }

    fun getUserQueueByQueueId(user: User, queueId: Long): UserQueue {
        return user.queues.firstOrNull { it.queue?.id == queueId }
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
            queueRepository.delete(userQueue.queue!!)
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

    fun joinQueue(token: String, queuePinCodeDTO: QueuePinCodeDTO) {
        val user = userService.getUserByToken(token)
        val queuePinCode = queuePinCodeRepository.findAll().firstOrNull { it.pinCode == queuePinCodeDTO.pinCode }
            ?: throw IllegalArgumentException("The pin code for queue is invalid: ${queuePinCodeDTO.pinCode}")
        if (user.queues.none { it.queue?.id == queuePinCode.queue?.id }) {
            val queueEntity = queueRepository.findAll().firstOrNull { it.id == queuePinCode.queue?.id }
                ?: throw IllegalArgumentException("The pin code for queue is invalid: ${queuePinCodeDTO.pinCode}")
            val userQueue =
                createUserQueueEntity(user, queueEntity)
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

    private fun transformUserQueueListToQueueShortDTO(
        userQueueList: List<UserQueue>
    ): List<QueueShortDTO> =
        userQueueList.map { it.queue }.map { q -> transformQueueToQueueShortDTO(q) }

    private fun transformQueueToQueueShortDTO(queue: Queue?): QueueShortDTO =
        QueueShortDTO(
            queueId = queue?.id!!,
            queueName = queue.name!!,
            queueColor = queue.color!!
        )

    private fun transformQueueToDTO(queue: Queue?, isActive: Boolean, userId: Long): QueueDTO = QueueDTO(
        queueId = queue?.id!!,
        queueName = queue.name!!,
        queueColor = queue.color!!,
        currentUser = transformUserToUserExpensesDTO(queue.currentUser, queue),
        isYourTurn = queue.currentUser?.id == userId,
        participants = sortUserExpensesDTOByFrozen(queue.userQueues
            .filter { it.user?.id != userId && it.user?.id != queue.currentUser?.id }
            .map { userQueue -> userQueue.user }
            .map { transformUserToUserExpensesDTO(it, queue) }),
        trackExpenses = queue.trackExpenses!!,
        isActive = isActive,
        isAdmin = queue.creator?.id == userId,
        link = queue.link!!
    )

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