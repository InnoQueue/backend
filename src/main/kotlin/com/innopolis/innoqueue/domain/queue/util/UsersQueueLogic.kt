package com.innopolis.innoqueue.domain.queue.util

import com.innopolis.innoqueue.dao.UserQueueRepository
import com.innopolis.innoqueue.domain.queue.dao.QueueRepository
import com.innopolis.innoqueue.domain.user.model.User
import com.innopolis.innoqueue.domain.user.service.UserService
import com.innopolis.innoqueue.model.UserQueue

/**
 * Util class for calculating which user has to be assigned to the queue
 */
object UsersQueueLogic {

    /**
     * Determines which user should be on duty for a particular queue
     * @param queue - queue to which new user has to be assigned
     */
    fun assignNextUser(
        queue: UserQueue,
        userService: UserService,
        userQueueRepository: UserQueueRepository,
        queueRepository: QueueRepository,
    ): User {
        val (usersInQueue, currentUserIndex) = getUsersInQueue(queue, userQueueRepository, userService, queueRepository)
        var index: Int = currentUserIndex!! + 1
        while (true) {
            if (index >= usersInQueue.size) {
                index = 0
            }
            val nextUser = usersInQueue[index]
            val nextUserQueue = userQueueRepository.findAll()
                .firstOrNull {
                    it.userQueueId?.userId == nextUser.id && it.userQueueId?.queueId == queue.userQueueId?.queueId
                }!!

            // If next user has skips < 0, then increment his skip and assign a queue to the user after him
            if (nextUserQueue.progress!! < 0) {
                nextUserQueue.progress = nextUserQueue.progress!! + 1
                userQueueRepository.save(nextUserQueue)
                index++
            } // We found the next candidate
            else {
                val queueToUpdate = queueRepository.findAll().firstOrNull { it.id == queue.userQueueId?.queueId }
                if (queueToUpdate != null) {
                    queueToUpdate.currentUser = nextUser
                    queueRepository.save(queueToUpdate)
                    return nextUser
                }
            }
        }
    }

    private fun getUsersInQueue(
        userQueue: UserQueue,
        userQueueRepository: UserQueueRepository,
        userService: UserService,
        queueRepository: QueueRepository
    ): Pair<List<User>, Int?> {
        val usersInQueue = userQueueRepository.findAll()
            .filter { it.userQueueId?.queueId == userQueue.userQueueId?.queueId }
            .filter { it.isActive == true }
            .sortedBy { it.dateJoined }
            .map { userService.findUserById(it.userQueueId?.userId!!)!! }

        val currentUserId =
            queueRepository.findAll().firstOrNull { it.id == userQueue.userQueueId?.queueId }?.currentUser?.id
        val currentUserIndex = usersInQueue
            .zip(usersInQueue.indices)
            .firstOrNull { (u, _) -> u.id == currentUserId }?.second ?: 0

        return usersInQueue to currentUserIndex
    }
}
