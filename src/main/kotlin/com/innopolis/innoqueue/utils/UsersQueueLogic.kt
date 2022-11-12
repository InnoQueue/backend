package com.innopolis.innoqueue.utils

import com.innopolis.innoqueue.dao.QueueRepository
import com.innopolis.innoqueue.dao.UserQueueRepository
import com.innopolis.innoqueue.models.User
import com.innopolis.innoqueue.models.UserQueue

object UsersQueueLogic {

    fun assignNextUser(
        queue: UserQueue,
        userQueueRepository: UserQueueRepository,
        queueRepository: QueueRepository
    ): User {
        val (usersInQueue, currentUserIndex) = getUsersInQueue(queue, userQueueRepository)
        var index: Int = currentUserIndex!! + 1
        while (true) {
            if (index >= usersInQueue.size) {
                index = 0
            }
            val nextUser = usersInQueue[index]
            val nextUserQueue = nextUser?.queues?.firstOrNull { q -> q.queue?.id == queue.queue?.id }!!

            // If next user has skips < 0, then increment his skip and assign a queue to the user after him
            if (nextUserQueue.skips!! < 0) {
                nextUserQueue.skips = nextUserQueue.skips!! + 1
                userQueueRepository.save(nextUserQueue)
                index++
            } // We found the next candidate
            else {
                val queueToUpdate = queueRepository.findAll().firstOrNull { it.id == queue.queue?.id }
                if (queueToUpdate != null) {
                    queueToUpdate.currentUser = nextUser
                    queueRepository.save(queueToUpdate)
                    return nextUser
                }
            }
        }
    }

    private fun getUsersInQueue(queue: UserQueue, userQueueRepository: UserQueueRepository): Pair<List<User?>, Int?> {
        val usersInQueue = userQueueRepository.findAll()
            .filter { it.queue?.id == queue.queue?.id }
            .sortedBy { it.dateJoined }
            .map { it.user }

        val currentUserIndex = usersInQueue
            .zip(usersInQueue.indices)
            .firstOrNull { (u, _) -> u?.id == queue.queue?.currentUser?.id }?.second ?: 0

        return usersInQueue to currentUserIndex
    }
}
