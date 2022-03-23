package com.innopolis.innoqueue.service

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
                    isAdmin = q.creator?.id == userId
                )
            }

    private fun transformUserToUserExpensesDTO(user: User?, queue: Queue): UserExpensesDTO = UserExpensesDTO(
        user?.name!!,
        queue.userQueues.firstOrNull { it.user?.id == user.id }?.expenses
    )
}