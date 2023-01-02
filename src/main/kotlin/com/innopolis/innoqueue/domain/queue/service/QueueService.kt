package com.innopolis.innoqueue.domain.queue.service

import com.innopolis.innoqueue.dao.UserQueueRepository
import com.innopolis.innoqueue.dao.UserQueuesShortForm
import com.innopolis.innoqueue.domain.queue.dao.QueueRepository
import com.innopolis.innoqueue.domain.queue.dto.*
import com.innopolis.innoqueue.domain.queue.model.Queue
import com.innopolis.innoqueue.domain.queue.util.UsersQueueLogic
import com.innopolis.innoqueue.domain.user.model.User
import com.innopolis.innoqueue.domain.user.service.UserService
import com.innopolis.innoqueue.dto.UserExpensesDTO
import com.innopolis.innoqueue.enums.NotificationsType
import com.innopolis.innoqueue.model.UserQueue
import com.innopolis.innoqueue.model.UserQueueId
import com.innopolis.innoqueue.service.NotificationsService
import com.innopolis.innoqueue.util.StringGenerator
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.time.ZoneOffset
import kotlin.math.abs

private const val PIN_CODE_LENGTH: Int = 6
private const val QR_CODE_LENGTH: Int = 48

/**
 * Service for working with queues
 */
@Suppress("TooManyFunctions")
@Service
class QueueService(
    private val userService: UserService,
    private val notificationService: NotificationsService,
    private val userQueueRepository: UserQueueRepository,
    private val queueRepository: QueueRepository
) {

    /**
     * Lists all queues for a particular user
     * @param token - user token
     */
    fun getQueues(token: String): QueuesListDTO {
        val (activeQueues, frozenQueues) = userQueueRepository.findAllUserQueueByToken(token)
            .partition { it.getIsActive() }
        return QueuesListDTO(activeQueues.convertToQueueShortDTO(), frozenQueues.convertToQueueShortDTO())
    }

    /**
     * Lists queue details
     * @param token - user token
     * @param queueId - id of a queue
     */
    fun getQueueById(token: String, queueId: Long): QueueDTO {
        val queueOptional = queueRepository.findById(queueId)
        if (!queueOptional.isPresent) {
            throw IllegalArgumentException("User does not belong to such queue: $queueId")
        }
        val userQueue = userQueueRepository.findUserQueueByToken(token, queueId)
            ?: throw IllegalArgumentException("User does not belong to such queue: $queueId")
        val queue = queueOptional.get()
        return QueueDTO(
            queueId = queue.id!!,
            queueName = queue.name!!,
            queueColor = queue.color!!,
            currentUser = transformUserToUserExpensesDTO(queue.currentUser, queue),
            isYourTurn = queue.currentUser?.id == userQueue.userQueueId?.userId,
            participants = getParticipants(queue),
            trackExpenses = queue.trackExpenses!!,
            isActive = userQueue.isActive!!,
            isAdmin = queue.creator?.id == userQueue.userQueueId?.userId,
            hashCode = getHashCode(queueId)
        )
    }

    /**
     * Return invite codes for a queue
     * @param token - user token
     * @param queueId - id of a queue
     */
    fun getQueueInviteCode(token: String, queueId: Long): QueueInviteCodeDTO {
        val userQueue = userQueueRepository.findUserQueueByToken(token, queueId)
            ?: throw IllegalArgumentException("User does not belong to such queue: $queueId")
        val queue = queueRepository.findAll().firstOrNull { it.id == userQueue.userQueueId?.queueId }!!
        return QueueInviteCodeDTO(
            pinCode = queue.getQueuePinCode(),
            qrCode = queue.getQueueQrCode()
        )
    }

    /**
     * Saves new queue
     * @param token - user token
     */
    fun createQueue(token: String, queue: NewQueueDTO): QueueDTO {
        val user = userService.findUserByToken(token)
        val createdQueue = saveQueueEntity(queue, user)
        saveUserQueueEntity(createdQueue, user)
        val qDTO = QueueDTO(
            queueId = createdQueue.id!!,
            queueName = createdQueue.name!!,
            queueColor = createdQueue.color!!,
            currentUser = transformUserToUserExpensesDTO(createdQueue.currentUser, createdQueue),
            isYourTurn = true,
            participants = emptyList(),
            trackExpenses = createdQueue.trackExpenses!!,
            isActive = true,
            isAdmin = true,
            hashCode = 0
        )
        qDTO.hashCode = getHashCode(qDTO)
        return qDTO
    }

    /**
     * Changes existing queue
     * @param token - user token
     */
    @Suppress("ThrowsCount")
    fun editQueue(token: String, editQueue: EditQueueDTO): QueueDTO {
        if (editQueue.queueId == null) {
            throw IllegalArgumentException("Queue id should be specified")
        }
        val user = userService.findUserByToken(token)
        val userQueue = getUserQueueByQueueId(user, editQueue.queueId)
        if (queueRepository.findAll().firstOrNull { it.id == userQueue.userQueueId?.queueId }?.creator?.id != user.id) {
            throw IllegalArgumentException("User is not an admin in this queue: ${editQueue.queueId}")
        }
        val queueEntity = queueRepository.findByIdOrNull(editQueue.queueId)
            ?: throw IllegalArgumentException("Queue does not exist. ID: ${editQueue.queueId}")

        var changed = false
        if (editQueue.name != null) {
            if (editQueue.name.isEmpty()) {
                throw IllegalArgumentException("Queue name can't be an empty string")
            }
            queueEntity.name = editQueue.name
            changed = true
        }
        if (editQueue.color != null) {
            if (editQueue.color.isEmpty()) {
                throw IllegalArgumentException("Queue color can't be an empty string")
            }
            queueEntity.color = editQueue.color
            changed = true
        }
        if (editQueue.trackExpenses != null) {
            queueEntity.trackExpenses = editQueue.trackExpenses
            changed = true
        }
        val updatedQueue = when (changed) {
            true -> queueRepository.save(queueEntity)
            false -> queueEntity
        }
        if (editQueue.participants != null) {
            val userToDelete = userQueueRepository
                .findAll()
                .filter { it.userQueueId?.queueId == updatedQueue.id }
                .filter { it.userQueueId?.userId !in editQueue.participants }
                .filter { it.userQueueId?.userId != user.id }
            if (userToDelete.isNotEmpty()) {
                userQueueRepository.deleteAll(userToDelete)
            }
        }

        return getQueueById(token, updatedQueue.id!!)
    }

    /**
     * Return user_queue model
     */
    fun getUserQueueByQueueId(user: User, queueId: Long): UserQueue {
        return userQueueRepository.findUserQueueByQueueId(queueId).firstOrNull { it.userQueueId?.userId == user.id }
            ?: throw IllegalArgumentException("User does not belong to such queue: $queueId")
    }

    /**
     * Change queue's freeze status
     */
    fun freezeUnFreezeQueue(token: String, queueId: Long, status: Boolean) {
        val user = userService.findUserByToken(token)
        val userQueue = getUserQueueByQueueId(user, queueId)
        when (status) {
            true -> {
                if (!userQueue.isActive!!) {
                    userQueue.isActive = true
                    userQueueRepository.save(userQueue)
                    notificationService.sendNotificationMessage(
                        NotificationsType.UNFROZEN,
                        user.id!!,
                        user.name!!,
                        userQueue.userQueueId?.queueId!!,
                        queueRepository.findAll().firstOrNull { it.id == userQueue.userQueueId?.queueId }!!.name!!
                    )
                }
            }

            false -> {
                if (userQueue.isActive!!) {
                    // You can't freeze queue if it's your turn
                    val queue = queueRepository.findAll().firstOrNull { it.id == userQueue.userQueueId?.queueId }!!
                    if (queue.currentUser?.id != user.id) {
                        userQueue.isActive = false
                        userQueueRepository.save(userQueue)
                        notificationService.sendNotificationMessage(
                            NotificationsType.FROZEN,
                            user.id!!,
                            user.name!!,
                            queue.id!!,
                            queue.name!!,
                        )
                    }
                }
            }
        }
    }

    /**
     * Deletes or leaves a queue
     * @param token - user token
     * @param queueId - id of a queue
     */
    fun deleteQueue(token: String, queueId: Long) {
        val user = userService.findUserByToken(token)
        val userQueue = getUserQueueByQueueId(user, queueId)
        // Delete queue
        val queue = queueRepository.findAll().firstOrNull { it.id == userQueue.userQueueId?.queueId }!!
        if (queue.creator?.id == user.id) {
            notificationService.sendNotificationMessage(
                NotificationsType.DELETE_QUEUE,
                user.id!!,
                user.name!!,
                queue.id!!,
                queue.name!!
            )
            queueRepository.delete(queue)
        } // Leave queue
        else {
            notificationService.sendNotificationMessage(
                NotificationsType.LEFT_QUEUE,
                user.id!!,
                user.name!!,
                queue.id!!,
                queue.name!!
            )
            userQueue.progress = 0
            // If it's your turn, reassign another user
            if (queue.currentUser?.id == user.id) {
                val nextUser = UsersQueueLogic.assignNextUser(
                    userQueue,
                    userService,
                    userQueueRepository,
                    queueRepository
                )
                notificationService.sendNotificationMessage(
                    NotificationsType.YOUR_TURN,
                    nextUser.id!!,
                    nextUser.name!!,
                    queue.id!!,
                    queue.name!!
                )
            }
            userQueueRepository.delete(userQueue)
        }
    }

    /**
     * Join queue via invite code
     * @param token - user token
     */
    @Suppress("ThrowsCount", "ReturnCount", "LongMethod")
    fun joinQueue(token: String, queueInviteCodeDTO: QueueInviteCodeDTO): QueueDTO {
        val user = userService.findUserByToken(token)

        if (queueInviteCodeDTO.pinCode != null) {
            val pinCode = queueInviteCodeDTO.pinCode
            val queue = queueRepository.findAll().firstOrNull { it.pinCode == pinCode }
                ?: throw IllegalArgumentException("The pin code for queue is invalid: $pinCode")

            val userQueue = userQueueRepository.findAll()
                .firstOrNull { it.userQueueId?.queueId == queue.id && it.userQueueId?.userId == user.id }

            if (userQueue == null) {

                val queueEntity = queueRepository.findAll().firstOrNull { it.id == queue.id }
                    ?: throw IllegalArgumentException("The pin code for queue is invalid: $pinCode")

                val newUserQueue = userQueueRepository.save(createUserQueueEntity(user, queueEntity))
                notificationService.sendNotificationMessage(
                    NotificationsType.JOINED_QUEUE,
                    user.id!!,
                    user.name!!,
                    queueEntity.id!!,
                    queueEntity.name!!
                )

                return transformQueueToDTO(
                    queue = queueEntity,
                    isActive = newUserQueue.isActive!!,
                    userId = user.id!!
                )
            }
            return transformQueueToDTO(
                queue = queue,
                isActive = userQueue.isActive!!,
                userId = user.id!!
            )
        } else if (queueInviteCodeDTO.qrCode != null) {
            val qrCode = queueInviteCodeDTO.qrCode
            val queue = queueRepository.findAll().firstOrNull { it.qrCode == qrCode }
                ?: throw IllegalArgumentException("The QR code for queue is invalid: $qrCode")
            val userQueue = userQueueRepository.findAll()
                .firstOrNull { it.userQueueId?.queueId == queue.id && it.userQueueId?.userId == user.id }
            if (userQueue == null) {
                val queueEntity = queueRepository.findAll().firstOrNull { it.id == queue.id }
                    ?: throw IllegalArgumentException("The QR code for queue is invalid: $qrCode")
                val newUserQueue = userQueueRepository.save(createUserQueueEntity(user, queueEntity))
                notificationService.sendNotificationMessage(
                    NotificationsType.JOINED_QUEUE,
                    user.id!!,
                    user.name!!,
                    queueEntity.id!!,
                    queueEntity.name!!
                )
                return transformQueueToDTO(
                    queue = queueEntity,
                    isActive = newUserQueue.isActive!!,
                    userId = user.id!!
                )
            }
            return transformQueueToDTO(
                queue = queue,
                isActive = userQueue.isActive!!,
                userId = user.id!!
            )
        } else {
            throw IllegalArgumentException("Provide qr_code or pin_code!")
        }
    }

    /**
     * Send a notification to user who is on duty for a particular queue
     * @param token - user token who sends notification
     * @param queueId - id of a queue
     */
    fun shakeUser(token: String, queueId: Long) {
        val user = userService.findUserByToken(token)
        // Check if user joined this queue
        getUserQueueByQueueId(user, queueId)
        val queue = queueRepository.findAll().firstOrNull { it?.id == queueId }
            ?: throw IllegalArgumentException("The queueId is invalid")
        if (queue.currentUser?.id == user.id) {
            throw IllegalArgumentException("You can't shake yourself!")
        }
        val currentUserQueue = queue.currentUser
        currentUserQueue?.let {
            queue.isImportant = true
            queueRepository.save(queue)
            notificationService.sendNotificationMessage(
                NotificationsType.SHOOK,
                it.id!!,
                it.name!!,
                queue.id!!,
                queue.name!!
            )
        }
    }

    private fun List<UserQueuesShortForm>.convertToQueueShortDTO(): List<QueueShortDTO> =
        this.map {
            QueueShortDTO(
                queueId = it.getQueueId(),
                queueName = it.getQueueName(),
                queueColor = it.getColor(),
                hashCode = getHashCode(it.getQueueId())
            )
        }

    @Suppress("MagicNumber")
    fun getHashCode(queue: QueueDTO): Int {
        val hashCodes =
            mutableListOf(
                queue.queueId.hashCode(),
                queue.queueName.hashCode(),
                queue.queueColor.hashCode(),
                queue.currentUser.hashCode(),
                queue.isYourTurn.hashCode(),
                queue.trackExpenses.hashCode(),
                queue.isActive.hashCode(),
                queue.isAdmin.hashCode()
            )
        for (p in queue.participants) {
            hashCodes.add(p.hashCode())
        }

        var hashCode = 0
        for (h in hashCodes) {
            hashCode = abs((((31 * hashCode) % 100000000) + (abs(h) % 100000000))) % 100000000
        }

        return hashCode
    }

    // TODO finish method
    @Suppress("FunctionOnlyReturningConstant", "UnusedPrivateMember")
    fun getHashCode(queueId: Long): Int {
        return 123
    }

    fun transformQueueToDTO(queue: Queue?, isActive: Boolean, userId: Long): QueueDTO {
        val qDTO = QueueDTO(
            queueId = queue?.id!!,
            queueName = queue.name!!,
            queueColor = queue.color!!,
            currentUser = transformUserToUserExpensesDTO(queue.currentUser, queue),
            isYourTurn = queue.currentUser?.id == userId,
            participants = getParticipants(queue),
            trackExpenses = queue.trackExpenses!!,
            isActive = isActive,
            isAdmin = queue.creator?.id == userId,
            hashCode = 0
        )
        qDTO.hashCode = getHashCode(qDTO)
        return qDTO
    }

    private fun getParticipants(queue: Queue): List<UserExpensesDTO> {
        val userQueueParticipants =
            userQueueRepository.findAll().filter { it.userQueueId?.queueId == queue.id }.sortedBy { it.dateJoined }

        val currentUserIndex = userQueueParticipants.indexOfFirst { it.userQueueId?.userId == queue.currentUser?.id }
        val participantsAfterCurrent =
            userQueueParticipants.slice((currentUserIndex + 1) until userQueueParticipants.size)
        val participantsBeforeCurrent = userQueueParticipants.slice(0 until currentUserIndex)

        val participants = mutableListOf<UserQueue>()
        participants.addAll(participantsAfterCurrent)
        participants.addAll(participantsBeforeCurrent)

        val (participantsWithAddProgress, participantsWithoutAddProgress) = participants.partition { it.progress!! < 0 }

        val sortedParticipants =
            participantsWithoutAddProgress + participantsWithAddProgress.sortedByDescending { it.progress }

        val (activeUsers, frozenUsers) = sortedParticipants.partition { it.isActive!! }
        val participantsResult = activeUsers + frozenUsers

        return participantsResult.map { userQueue -> userService.findUserById(userQueue.userQueueId?.userId!!) }
            .map { transformUserToUserExpensesDTO(it, queue) }
    }

    private fun transformUserToUserExpensesDTO(user: User?, queue: Queue): UserExpensesDTO {
        val isActive = userQueueRepository.findAll()
            .firstOrNull { it.userQueueId?.queueId == queue.id && it.userQueueId?.userId == user?.id }?.isActive
            ?: true
        return UserExpensesDTO(
            user?.id!!,
            user.name!!,
            userQueueRepository.findAll()
                .firstOrNull { it.userQueueId?.queueId == queue.id && it.userQueueId?.userId == user.id }?.expenses,
            isActive
        )
    }

    private fun saveQueueEntity(queue: NewQueueDTO, user: User): Queue {
        if (queue.name.isEmpty()) {
            throw IllegalArgumentException("Queue name can't be an empty string")
        }
        if (queue.color.isEmpty()) {
            throw IllegalArgumentException("Queue color can't be an empty string")
        }
        val queueEntity = Queue()
        queueEntity.name = queue.name
        queueEntity.color = queue.color
        queueEntity.creator = user
        queueEntity.trackExpenses = queue.trackExpenses
        queueEntity.currentUser = user
        return queueRepository.save(queueEntity)
    }

    private fun saveUserQueueEntity(queue: Queue, user: User): UserQueue {
        val userQueueEntity = createUserQueueEntity(user, queue)
        return userQueueRepository.save(userQueueEntity)
    }

    private fun createUserQueueEntity(user: User, queue: Queue): UserQueue {
        val userQueue = UserQueue()
        userQueue.userQueueId = UserQueueId().apply {
            queueId = queue.id!!
            userId = user.id!!
        }
        userQueue.isActive = true
        userQueue.progress = 0
        userQueue.completes = 0
        userQueue.skips = 0
        userQueue.expenses = 0L
        userQueue.dateJoined = LocalDateTime.now()
        return userQueue
    }

    private fun Queue.getQueuePinCode() = this.pinCode ?: run {
        val pinCode = generateUniquePinCode(queueRepository.findPinCodes())
        this.pinCode = pinCode
        this.pinDateCreated = LocalDateTime.now(ZoneOffset.UTC)
        queueRepository.save(this)
        pinCode
    }

    private fun Queue.getQueueQrCode() = this.qrCode ?: run {
        val qrCode = generateUniqueQrCode(queueRepository.findQrCodes())
        this.qrCode = qrCode
        this.qrDateCreated = LocalDateTime.now(ZoneOffset.UTC)
        queueRepository.save(this)
        qrCode
    }

    private fun generateUniqueQrCode(qrCodes: List<String?>): String {
        val generator = StringGenerator(QR_CODE_LENGTH)
        while (true) {
            val newQrCode = generator.generateString()
            if (newQrCode !in qrCodes) {
                return newQrCode
            }
        }
    }

    @Suppress("MagicNumber")
    private fun generateUniquePinCode(pinCodes: List<String>): String {
        while (true) {
            val newPinCode = (1..PIN_CODE_LENGTH)
                .map { (0..9).random() }
                .fold("") { acc: String, i: Int -> acc + i.toString() }
            if (newPinCode !in pinCodes) {
                return newPinCode
            }
        }
    }
}
