package com.innopolis.innoqueue.services

import com.innopolis.innoqueue.dao.QueuePinCodeRepository
import com.innopolis.innoqueue.dao.QueueQrCodeRepository
import com.innopolis.innoqueue.dao.QueueRepository
import com.innopolis.innoqueue.dao.UserQueueRepository
import com.innopolis.innoqueue.dto.*
import com.innopolis.innoqueue.enums.NotificationsType
import com.innopolis.innoqueue.models.*
import com.innopolis.innoqueue.utils.StringGenerator
import com.innopolis.innoqueue.utils.UsersQueueLogic
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.time.ZoneOffset
import kotlin.math.abs

private const val PIN_CODE_LENGTH: Int = 6
private const val QR_CODE_LENGTH: Int = 48

@Suppress("TooManyFunctions")
@Service
class QueueService(
    private val userService: UserService,
    private val notificationService: NotificationsService,
    private val userQueueRepository: UserQueueRepository,
    private val queueRepository: QueueRepository,
    private val queuePinCodeRepository: QueuePinCodeRepository,
    private val queueQrCodeRepository: QueueQrCodeRepository
) {
    fun getQueues(token: String): QueuesListDTO {
        val user = userService.findUserByToken(token)
        val (activeQueue, frozenQueue) = user.queues.partition { it.isActive!! }
        return QueuesListDTO(
            transformUserQueueListToQueueShortDTO(activeQueue).sortedBy { it.queueName },
            transformUserQueueListToQueueShortDTO(frozenQueue).sortedBy { it.queueName }
        )
    }

    fun getQueueById(token: String, queueId: Long): QueueDTO {
        val user = userService.findUserByToken(token)
        val userQueue: UserQueue = getUserQueueByQueueId(user, queueId)
        return transformQueueToDTO(queue = userQueue.queue, isActive = userQueue.isActive!!, userId = user.id!!)
    }

    fun getQueueInviteCode(token: String, queueId: Long): QueueInviteCodeDTO {
        val user = userService.findUserByToken(token)
        val userQueue: UserQueue = getUserQueueByQueueId(user, queueId)
        val pinCode = getQueuePinCode(userQueue)
        val qrCode = getQueueQrCode(userQueue)
        return QueueInviteCodeDTO(pinCode = pinCode, qrCode = qrCode)
    }

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

    @Suppress("ThrowsCount")
    fun editQueue(token: String, editQueue: EditQueueDTO): QueueDTO {
        if (editQueue.queueId == null) {
            throw IllegalArgumentException("Queue id should be specified")
        }
        val user = userService.findUserByToken(token)
        val userQueue = getUserQueueByQueueId(user, editQueue.queueId)
        if (userQueue.queue?.creator?.id != user.id) {
            throw IllegalArgumentException("User is not an admin in this queue: ${editQueue.queueId}")
        }
        val queueEntity = queueRepository.findByIdOrNull(editQueue.queueId)
            ?: throw NoSuchElementException("Queue does not exist. ID: ${editQueue.queueId}")

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
                .filter { it.queue?.id == updatedQueue.id }
                .filter { it.user?.id !in editQueue.participants }
                .filter { it.user?.id != user.id }
            if (userToDelete.isNotEmpty()) {
                userQueueRepository.deleteAll(userToDelete)
            }
        }

        return getQueueById(token, updatedQueue.id!!)
    }

    fun getUserQueueByQueueId(user: User, queueId: Long): UserQueue {
        return user.queues.firstOrNull { it.queue?.id == queueId }
            ?: throw IllegalArgumentException("User does not belong to such queue: $queueId")
    }

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
                        userQueue.queue?.id!!,
                        userQueue.queue?.name!!
                    )
                }
            }

            false -> {
                if (userQueue.isActive!!) {
                    // You can't freeze queue if it's your turn
                    if (userQueue.queue?.currentUser?.id != user.id) {
                        userQueue.isActive = false
                        userQueueRepository.save(userQueue)
                        notificationService.sendNotificationMessage(
                            NotificationsType.FROZEN,
                            user.id!!,
                            user.name!!,
                            userQueue.queue?.id!!,
                            userQueue.queue?.name!!,
                        )
                    }
                }
            }
        }
    }

    fun deleteQueue(token: String, queueId: Long) {
        val user = userService.findUserByToken(token)
        val userQueue = getUserQueueByQueueId(user, queueId)
        // Delete queue
        if (userQueue.queue?.creator?.id == user.id) {
            notificationService.sendNotificationMessage(
                NotificationsType.DELETE_QUEUE,
                user.id!!,
                user.name!!,
                userQueue.queue?.id!!,
                userQueue.queue?.name!!
            )
            queueRepository.delete(userQueue.queue!!)
        } // Leave queue
        else {
            notificationService.sendNotificationMessage(
                NotificationsType.LEFT_QUEUE,
                user.id!!,
                user.name!!,
                userQueue.queue?.id!!,
                userQueue.queue?.name!!
            )
            userQueue.skips = 0
            // If it's your turn, reassign another user
            if (userQueue.queue?.currentUser?.id == user.id) {
                val nextUser = UsersQueueLogic.assignNextUser(userQueue, userQueueRepository, queueRepository)
                notificationService.sendNotificationMessage(
                    NotificationsType.YOUR_TURN,
                    nextUser.id!!,
                    nextUser.name!!,
                    userQueue.queue?.id!!,
                    userQueue.queue?.name!!
                )
            }
            userQueueRepository.delete(userQueue)
        }
    }

    @Suppress("ThrowsCount", "ReturnCount")
    fun joinQueue(token: String, queueInviteCodeDTO: QueueInviteCodeDTO): QueueDTO {
        val user = userService.findUserByToken(token)

        if (queueInviteCodeDTO.pinCode != null) {
            val pinCode = queueInviteCodeDTO.pinCode
            val queuePinCode = queuePinCodeRepository.findAll().firstOrNull { it.pinCode == pinCode }
                ?: throw IllegalArgumentException("The pin code for queue is invalid: $pinCode")

            val userQueue = user.queues.firstOrNull { it.queue?.id == queuePinCode.queue?.id }

            if (userQueue == null) {

                val queueEntity = queueRepository.findAll().firstOrNull { it.id == queuePinCode.queue?.id }
                    ?: throw IllegalArgumentException("The pin code for queue is invalid: $pinCode")

                val newUserQueue = userQueueRepository.save(createUserQueueEntity(user, queueEntity))
                notificationService.sendNotificationMessage(
                    NotificationsType.JOINED_QUEUE,
                    user.id!!,
                    user.name!!,
                    newUserQueue.queue?.id!!,
                    newUserQueue.queue?.name!!
                )

                return transformQueueToDTO(
                    queue = newUserQueue.queue,
                    isActive = newUserQueue.isActive!!,
                    userId = user.id!!
                )
            }
            return transformQueueToDTO(queue = userQueue.queue, isActive = userQueue.isActive!!, userId = user.id!!)
        } else if (queueInviteCodeDTO.qrCode != null) {
            val qrCode = queueInviteCodeDTO.qrCode
            val queueQrCode = queueQrCodeRepository.findAll().firstOrNull { it.qrCode == qrCode }
                ?: throw IllegalArgumentException("The QR code for queue is invalid: $qrCode")
            val userQueue = user.queues.firstOrNull { it.queue?.id == queueQrCode.queue?.id }
            if (userQueue == null) {
                val queueEntity = queueRepository.findAll().firstOrNull { it.id == queueQrCode.queue?.id }
                    ?: throw IllegalArgumentException("The QR code for queue is invalid: $qrCode")
                val newUserQueue = userQueueRepository.save(createUserQueueEntity(user, queueEntity))
                notificationService.sendNotificationMessage(
                    NotificationsType.JOINED_QUEUE,
                    user.id!!,
                    user.name!!,
                    newUserQueue.queue?.id!!,
                    newUserQueue.queue?.name!!
                )
                return transformQueueToDTO(
                    queue = newUserQueue.queue,
                    isActive = newUserQueue.isActive!!,
                    userId = user.id!!
                )
            }
            return transformQueueToDTO(queue = userQueue.queue, isActive = userQueue.isActive!!, userId = user.id!!)
        } else {
            throw IllegalArgumentException("Provide qr_code or pin_code!")
        }
    }

    fun shakeUser(token: String, queueId: Long) {
        val user = userService.findUserByToken(token)
        val userQueue = getUserQueueByQueueId(user, queueId)
        if (userQueue.queue?.currentUser?.id == user.id) {
            throw IllegalArgumentException("You can't shake yourself!")
        }

        val participantQueue = userQueue.queue?.currentUser?.queues?.firstOrNull { it.queue?.id == queueId }
            ?: throw IllegalArgumentException("The queueId is invalid")
        participantQueue.isImportant = true
        userQueueRepository.save(participantQueue)
        notificationService.sendNotificationMessage(
            NotificationsType.SHOOK,
            participantQueue.user?.id!!,
            participantQueue.user?.name!!,
            userQueue.queue?.id!!,
            userQueue.queue?.name!!
        )
    }

    private fun transformUserQueueListToQueueShortDTO(
        userQueueList: List<UserQueue>
    ): List<QueueShortDTO> =
        userQueueList.map { q -> transformQueueToQueueShortDTO(q) }

    private fun transformQueueToQueueShortDTO(userQueue: UserQueue): QueueShortDTO {
        val queue = userQueue.queue
        return QueueShortDTO(
            queueId = queue?.id!!,
            queueName = queue.name!!,
            queueColor = queue.color!!,
            hashCode = getHashCode(
                transformQueueToDTO(
                    queue = queue,
                    isActive = userQueue.isActive!!,
                    userId = userQueue.user?.id!!
                )
            )
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
        val userQueueParticipants = queue.userQueues.sortedBy { it.dateJoined }

        val currentUserIndex = userQueueParticipants.indexOfFirst { it.user?.id == queue.currentUser?.id }
        val participantsAfterCurrent =
            userQueueParticipants.slice((currentUserIndex + 1) until userQueueParticipants.size)
        val participantsBeforeCurrent = userQueueParticipants.slice(0 until currentUserIndex)

        val participants = mutableListOf<UserQueue>()
        participants.addAll(participantsAfterCurrent)
        participants.addAll(participantsBeforeCurrent)

        val (participantsWithAddProgress, participantsWithoutAddProgress) = participants.partition { it.skips!! < 0 }

        val sortedParticipants =
            participantsWithoutAddProgress + participantsWithAddProgress.sortedByDescending { it.skips }

        val (activeUsers, frozenUsers) = sortedParticipants.partition { it.isActive!! }
        val participantsResult = activeUsers + frozenUsers

        return participantsResult.map { userQueue -> userQueue.user }.map { transformUserToUserExpensesDTO(it, queue) }
    }

    private fun transformUserToUserExpensesDTO(user: User?, queue: Queue): UserExpensesDTO {
        val isActive = user?.queues?.firstOrNull { it.queue?.id == queue.id }?.isActive ?: true
        return UserExpensesDTO(
            user?.id!!,
            user.name!!,
            queue.userQueues.firstOrNull { it.user?.id == user.id }?.expenses,
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
        userQueue.queue = queue
        userQueue.user = user
        userQueue.isActive = true
        userQueue.skips = 0
        userQueue.expenses = 0.0
        userQueue.isImportant = false
        userQueue.dateJoined = LocalDateTime.now()
        return userQueue
    }

    private fun getQueuePinCode(userQueue: UserQueue): String {
        val queuePinCodes = queuePinCodeRepository.findAll()
        val queuePinCode = queuePinCodes.firstOrNull { it.queue?.id == userQueue.queue?.id }
        return if (queuePinCode == null) {
            val pinCode = generateUniquePinCode(queuePinCodes.map { it.pinCode })
            val newQueuePinCode = QueuePinCode()
            newQueuePinCode.queue = userQueue.queue
            newQueuePinCode.pinCode = pinCode
            newQueuePinCode.dateCreated = LocalDateTime.now(ZoneOffset.UTC)
            queuePinCodeRepository.save(newQueuePinCode)
            pinCode
        } else queuePinCode.pinCode!!
    }

    private fun getQueueQrCode(userQueue: UserQueue): String {
        val queueQrCodes = queueQrCodeRepository.findAll()
        val queueQrCode = queueQrCodes.firstOrNull { it.queue?.id == userQueue.queue?.id }
        return if (queueQrCode == null) {
            val qrCode = generateUniqueQRCode(queueQrCodes.map { it.qrCode })
            val newQueueQrCode = QueueQrCode()
            newQueueQrCode.queue = userQueue.queue
            newQueueQrCode.qrCode = qrCode
            newQueueQrCode.dateCreated = LocalDateTime.now(ZoneOffset.UTC)
            queueQrCodeRepository.save(newQueueQrCode)
            qrCode
        } else queueQrCode.qrCode!!
    }

    private fun generateUniqueQRCode(qrCodes: List<String?>): String {
        val generator = StringGenerator(QR_CODE_LENGTH)
        while (true) {
            val newQrCode = generator.generateString()
            if (newQrCode !in qrCodes) {
                return newQrCode
            }
        }
    }

    @Suppress("MagicNumber")
    private fun generateUniquePinCode(pinCodes: List<String?>): String {
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
