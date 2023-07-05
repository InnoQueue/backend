package com.innopolis.innoqueue.domain.queue.service

import com.innopolis.innoqueue.domain.notification.enums.NotificationType
import com.innopolis.innoqueue.domain.notification.service.NotificationService
import com.innopolis.innoqueue.domain.queue.dao.QueueRepository
import com.innopolis.innoqueue.domain.queue.dto.*
import com.innopolis.innoqueue.domain.queue.model.Queue
import com.innopolis.innoqueue.domain.queue.util.UsersQueueLogic
import com.innopolis.innoqueue.domain.user.model.User
import com.innopolis.innoqueue.domain.user.service.UserService
import com.innopolis.innoqueue.domain.userqueue.dao.UserQueueRepository
import com.innopolis.innoqueue.domain.userqueue.model.UserQueue
import com.innopolis.innoqueue.domain.userqueue.model.UserQueueId
import com.innopolis.innoqueue.domain.userqueue.model.UserQueuesShortForm
import com.innopolis.innoqueue.util.StringGenerator
import org.slf4j.LoggerFactory
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.time.ZoneOffset

private const val PIN_CODE_LENGTH: Int = 6
private const val QR_CODE_LENGTH: Int = 48

/**
 * Service for working with queues
 */
@Suppress("TooManyFunctions")
@Service
class QueueService(
    private val userService: UserService,
    private val notificationService: NotificationService,
    private val userQueueRepository: UserQueueRepository,
    private val queueRepository: QueueRepository
) {
    private val logger = LoggerFactory.getLogger(javaClass)
    /**
     * Lists all queues for a particular user
     * @param token - user token
     */
    @Transactional
    fun getQueues(token: String, sortOption: String? = null): QueuesListDto {
        logger.info("user-token=$token")
        return when (sortOption) {
            null, "queue" -> QueuesListDto(userQueueRepository.findAllUserQueueByToken(token).convertToQueueShortDTO())
            "participant" -> QueuesListDto(
                userQueueRepository.findAllUserQueueByToken(token)
                    .sortedBy { it.getOnDutyUserName() }
                    .convertToQueueShortDTO()
            )

            "date" -> QueuesListDto(
                userQueueRepository.findAllUserQueueByToken(token).sortedBy { it.getDateJoined() }
                    .convertToQueueShortDTO()
            )
            // TODO finish method
            "todo" -> QueuesListDto(userQueueRepository.findAllUserQueueByToken(token).convertToQueueShortDTO())
            else -> throw IllegalArgumentException(
                "Sort option should be: 'queue', 'participant', 'date' or 'todo'. Provided: $this"
            )
        }
    }

    /**
     * Lists queue details
     * @param token - user token
     * @param queueId - id of a queue
     */
    @Transactional
    fun getQueueById(token: String, queueId: Long): QueueDetailsDto {
        logger.info("user-token=$token")
        val queueOptional = queueRepository.findById(queueId)
        require(queueOptional.isPresent) { "User does not belong to such queue: $queueId" }
        val userQueue = userQueueRepository.findUserQueueByToken(token, queueId)
            ?: throw IllegalArgumentException("User does not belong to such queue: $queueId")
        val queue = queueOptional.get()
        return QueueDetailsDto(
            queueId = queue.queueId!!,
            queueName = queue.name!!,
            queueColor = queue.color!!,
//            currentUser = transformUserToUserExpensesDTO(queue.currentUserId, queue, true),
//            yourTurn = queue.currentUserId == userQueue.userQueueId?.userId,
            participants = listOf(
                transformUserToUserExpensesDTO(queue.currentUserId, queue, true)
            ) + getParticipants(
                queue
            ),
            trackExpenses = queue.trackExpenses!!,
            admin = queue.creatorId == userQueue.userQueueId?.userId
        )
    }

    /**
     * Return invite codes for a queue
     * @param token - user token
     * @param queueId - id of a queue
     */
    @Transactional
    fun getQueueInviteCode(token: String, queueId: Long): QueueInviteCodeDto {
        logger.info("user-token=$token")
        val userQueue = userQueueRepository.findUserQueueByToken(token, queueId)
            ?: throw IllegalArgumentException("User does not belong to such queue: $queueId")
        val queue = queueRepository.findAll().firstOrNull { it.queueId == userQueue.userQueueId?.queueId }!!
        return QueueInviteCodeDto(
            pinCode = queue.getQueuePinCode(),
            qrCode = queue.getQueueQrCode()
        )
    }

    /**
     * Saves new queue
     * @param token - user token
     */
    @Transactional
    fun createQueue(token: String, queue: NewQueueDto): QueueDetailsDto {
        logger.info("user-token=$token")
        val user = userService.findUserByToken(token)
        val createdQueue = saveQueueEntity(queue, user)
        saveUserQueueEntity(createdQueue, user)
        val qDTO = QueueDetailsDto(
            queueId = createdQueue.queueId!!,
            queueName = createdQueue.name!!,
            queueColor = createdQueue.color!!,
//            currentUser = transformUserToUserExpensesDTO(createdQueue.currentUserId, createdQueue, true),
//            yourTurn = true,
            participants = listOf(
                transformUserToUserExpensesDTO(createdQueue.currentUserId, createdQueue, true)
            ),
            trackExpenses = createdQueue.trackExpenses!!,
            admin = true
        )
        return qDTO
    }

    /**
     * Changes existing queue
     * @param token - user token
     */
    @Suppress("ThrowsCount")
    @Transactional
    fun editQueue(token: String, queueId: Long, editQueue: EditQueueDto): QueueDetailsDto {
        logger.info("user-token=$token")
        val user = userService.findUserByToken(token)
        val userQueue = getUserQueueByQueueId(user, queueId)
        require(
            queueRepository.findAll()
                .firstOrNull { it.queueId == userQueue.userQueueId?.queueId }?.creatorId == user.id
        ) { "User is not an admin in this queue: $queueId" }
        val queueEntity = queueRepository.findByIdOrNull(queueId)
            ?: throw IllegalArgumentException("Queue does not exist. ID: $queueId")

        var changed = false
        if (editQueue.queueName != null) {
            require(editQueue.queueName.isNotEmpty()) { "Queue name can't be an empty string" }
            queueEntity.name = editQueue.queueName
            changed = true
        }
        if (editQueue.queueColor != null) {
            require(editQueue.queueColor.isNotEmpty()) { "Queue color can't be an empty string" }
            queueEntity.color = editQueue.queueColor
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
            val usersToDelete = userQueueRepository
                .findAll()
                .filter { it.userQueueId?.queueId == updatedQueue.queueId }
                .filter { it.userQueueId?.userId !in editQueue.participants }
                .filter { it.userQueueId?.userId != user.id }
            if (usersToDelete.isNotEmpty()) {
                if (updatedQueue.currentUserId in usersToDelete.mapNotNull { it.userQueueId?.userId }) {
                    userQueueRepository.deleteAll(usersToDelete)
                    val nextUserId = userQueueRepository
                        .findAll()
                        .filter { it.userQueueId?.queueId == updatedQueue.queueId }
                        .firstNotNullOfOrNull { it.userQueueId?.userId }
                    updatedQueue.currentUserId = nextUserId
                    queueRepository.save(updatedQueue)
                } else {
                    userQueueRepository.deleteAll(usersToDelete)
                }
            }
        }

        return getQueueById(token, updatedQueue.queueId!!)
    }

    /**
     * Return user_queue model
     */
    @Transactional
    fun getUserQueueByQueueId(user: User, queueId: Long): UserQueue {
        return userQueueRepository.findUserQueueByQueueId(queueId).firstOrNull { it.userQueueId?.userId == user.id }
            ?: throw IllegalArgumentException("User does not belong to such queue: $queueId")
    }

    /**
     * Change queue's freeze status
     */
    @Transactional
    fun freezeUnFreezeQueue(token: String, queueId: Long, status: Boolean) {
        logger.info("user-token=$token")
        val user = userService.findUserByToken(token)
        val userQueue = getUserQueueByQueueId(user, queueId)
        when (status) {
            true -> {
                if (!userQueue.isActive!!) {
                    userQueue.isActive = true
                    userQueueRepository.save(userQueue)
                    notificationService.sendNotificationMessage(
                        NotificationType.UNFROZEN,
                        user.id!!,
                        user.name!!,
                        userQueue.userQueueId?.queueId!!,
                        queueRepository.findAll().firstOrNull { it.queueId == userQueue.userQueueId?.queueId }!!.name!!
                    )
                }
            }

            false -> {
                if (userQueue.isActive!!) {
                    // You can't freeze queue if it's your turn
                    val queue = queueRepository.findAll().firstOrNull { it.queueId == userQueue.userQueueId?.queueId }!!
                    if (queue.currentUserId != user.id) {
                        userQueue.isActive = false
                        userQueueRepository.save(userQueue)
                        notificationService.sendNotificationMessage(
                            NotificationType.FROZEN,
                            user.id!!,
                            user.name!!,
                            queue.queueId!!,
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
    @Transactional
    fun deleteQueue(token: String, queueId: Long) {
        logger.info("user-token=$token")
        val user = userService.findUserByToken(token)
        val userQueue = getUserQueueByQueueId(user, queueId)
        // Delete queue
        val queue = queueRepository.findAll().firstOrNull { it.queueId == userQueue.userQueueId?.queueId }!!
        if (queue.creatorId == user.id) {
            notificationService.sendNotificationMessage(
                NotificationType.DELETE_QUEUE,
                user.id!!,
                user.name!!,
                queue.queueId!!,
                queue.name!!
            )
            queueRepository.delete(queue)
        } // Leave queue
        else {
            notificationService.sendNotificationMessage(
                NotificationType.LEFT_QUEUE,
                user.id!!,
                user.name!!,
                queue.queueId!!,
                queue.name!!
            )
            userQueue.progress = 0
            // If it's your turn, reassign another user
            if (queue.currentUserId == user.id) {
                val nextUser = UsersQueueLogic.assignNextUser(
                    userQueue,
                    userService,
                    userQueueRepository,
                    queueRepository
                )
                notificationService.sendNotificationMessage(
                    NotificationType.YOUR_TURN,
                    nextUser.id!!,
                    nextUser.name!!,
                    queue.queueId!!,
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
    @Transactional
    fun joinQueue(token: String, queueInviteCodeDTO: QueueInviteCodeDto): QueueDetailsDto {
        logger.info("user-token=$token")
        val user = userService.findUserByToken(token)

        require(queueInviteCodeDTO.pinCode != null || queueInviteCodeDTO.qrCode != null) {
            "Provide qrCode or pinCode!"
        }
        if (queueInviteCodeDTO.pinCode != null) {
            val pinCode = queueInviteCodeDTO.pinCode
            val queue = queueRepository.findAll().firstOrNull { it.pinCode == pinCode }
                ?: throw IllegalArgumentException("The pin code for queue is invalid: $pinCode")

            val userQueue = userQueueRepository.findAll()
                .firstOrNull { it.userQueueId?.queueId == queue.queueId && it.userQueueId?.userId == user.id }

            if (userQueue == null) {

                val queueEntity = queueRepository.findAll().firstOrNull { it.queueId == queue.queueId }
                    ?: throw IllegalArgumentException("The pin code for queue is invalid: $pinCode")

                userQueueRepository.save(createUserQueueEntity(user, queueEntity))
                notificationService.sendNotificationMessage(
                    NotificationType.JOINED_QUEUE,
                    user.id!!,
                    user.name!!,
                    queueEntity.queueId!!,
                    queueEntity.name!!
                )

                return transformQueueToDTO(
                    queue = queueEntity,
                    userId = user.id!!
                )
            }
            return transformQueueToDTO(
                queue = queue,
                userId = user.id!!
            )
        } else {
            val qrCode = queueInviteCodeDTO.qrCode
            val queue = queueRepository.findAll().firstOrNull { it.qrCode == qrCode }
                ?: throw IllegalArgumentException("The QR code for queue is invalid: $qrCode")
            val userQueue = userQueueRepository.findAll()
                .firstOrNull { it.userQueueId?.queueId == queue.queueId && it.userQueueId?.userId == user.id }
            if (userQueue == null) {
                val queueEntity = queueRepository.findAll().firstOrNull { it.queueId == queue.queueId }
                    ?: throw IllegalArgumentException("The QR code for queue is invalid: $qrCode")
                userQueueRepository.save(createUserQueueEntity(user, queueEntity))
                notificationService.sendNotificationMessage(
                    NotificationType.JOINED_QUEUE,
                    user.id!!,
                    user.name!!,
                    queueEntity.queueId!!,
                    queueEntity.name!!
                )
                return transformQueueToDTO(
                    queue = queueEntity,
                    userId = user.id!!
                )
            }
            return transformQueueToDTO(
                queue = queue,
                userId = user.id!!
            )
        }
    }

    /**
     * Send a notification to user who is on duty for a particular queue
     * @param token - user token who sends notification
     * @param queueId - id of a queue
     */
    @Transactional
    fun shakeUser(token: String, queueId: Long) {
        logger.info("user-token=$token")
        val user = userService.findUserByToken(token)
        // Check if user joined this queue
        getUserQueueByQueueId(user, queueId)
        val queue = queueRepository.findAll().firstOrNull { it?.queueId == queueId }
            ?: throw IllegalArgumentException("The queueId is invalid")
        require(queue.currentUserId != user.id) { "You can't shake yourself!" }
        val currentUserQueue = queue.currentUserId
        currentUserQueue?.let {
            queue.isImportant = true
            queueRepository.save(queue)
            notificationService.sendNotificationMessage(
                NotificationType.SHOOK,
                it,
                userService.findUserNameById(it)!!,
                queue.queueId!!,
                queue.name!!
            )
        }
    }

    private fun List<UserQueuesShortForm>.convertToQueueShortDTO(): List<QueueShortDto> =
        this.map {
            QueueShortDto(
                queueId = it.getQueueId(),
                queueName = it.getQueueName(),
                queueColor = it.getColor(),
                onDutyUserName = it.getOnDutyUserName(),
                active = it.getIsActive()
            )
        }

    @Transactional
    fun transformQueueToDTO(queue: Queue?, userId: Long): QueueDetailsDto {
        val qDTO = QueueDetailsDto(
            queueId = queue?.queueId!!,
            queueName = queue.name!!,
            queueColor = queue.color!!,
//            currentUser = transformUserToUserExpensesDTO(queue.currentUserId, queue, true),
//            yourTurn = queue.currentUserId == userId,
            participants = listOf(
                transformUserToUserExpensesDTO(queue.currentUserId, queue, true)
            ) + getParticipants(queue),
            trackExpenses = queue.trackExpenses!!,
            admin = queue.creatorId == userId
        )
        return qDTO
    }

    private fun getParticipants(queue: Queue): List<QueueParticipantDto> {
        val userQueueParticipants =
            userQueueRepository.findAll().filter { it.userQueueId?.queueId == queue.queueId }.sortedBy { it.dateJoined }

        val currentUserIndex = userQueueParticipants.indexOfFirst { it.userQueueId?.userId == queue.currentUserId }
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

        return participantsResult.map { userQueue ->
            transformUserToUserExpensesDTO(
                userQueue.userQueueId?.userId!!,
                queue,
                false
            )
        }
    }

    private fun transformUserToUserExpensesDTO(userId: Long?, queue: Queue, isOnDuty: Boolean): QueueParticipantDto {
        val user = userService.findUserById(userId!!)
        val isActive = userQueueRepository.findAll()
            .firstOrNull { it.userQueueId?.queueId == queue.queueId && it.userQueueId?.userId == user?.id }?.isActive
            ?: true
        return QueueParticipantDto(
            user?.id!!,
            user.name!!,
            userQueueRepository.findAll()
                .firstOrNull {
                    it.userQueueId?.queueId == queue.queueId && it.userQueueId?.userId == user.id
                }?.expenses,
            isActive,
            isOnDuty
        )
    }

    private fun saveQueueEntity(queue: NewQueueDto, user: User): Queue {
        require(queue.queueName.isNotEmpty()) { "Queue name can't be an empty string" }
        require(queue.queueColor.isNotEmpty()) { "Queue color can't be an empty string" }
        val queueEntity = Queue()
        queueEntity.name = queue.queueName
        queueEntity.color = queue.queueColor
        queueEntity.creatorId = user.id
        queueEntity.trackExpenses = queue.trackExpenses
        queueEntity.currentUserId = user.id
        return queueRepository.save(queueEntity)
    }

    private fun saveUserQueueEntity(queue: Queue, user: User): UserQueue {
        val userQueueEntity = createUserQueueEntity(user, queue)
        return userQueueRepository.save(userQueueEntity)
    }

    private fun createUserQueueEntity(user: User, queue: Queue): UserQueue {
        val userQueue = UserQueue()
        userQueue.userQueueId = UserQueueId().apply {
            queueId = queue.queueId!!
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
