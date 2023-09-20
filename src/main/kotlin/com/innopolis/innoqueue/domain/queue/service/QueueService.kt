package com.innopolis.innoqueue.domain.queue.service

import com.innopolis.innoqueue.domain.queue.dto.*
import com.innopolis.innoqueue.domain.user.model.User
import com.innopolis.innoqueue.domain.userqueue.model.UserQueue

interface QueueService {
    fun getQueues(token: String, sortOption: String? = null): QueuesListDto

    fun getQueueById(token: String, queueId: Long): QueueDetailsDto

    fun getQueueInviteCode(token: String, queueId: Long): QueueInviteCodeDto

    fun createQueue(token: String, queue: NewQueueDto): QueueDetailsDto

    fun editQueue(token: String, queueId: Long, editQueue: EditQueueDto): QueueDetailsDto

    fun getUserQueueByQueueId(user: User, queueId: Long): UserQueue

    fun freezeUnFreezeQueue(token: String, queueId: Long, status: Boolean)

    fun deleteQueue(token: String, queueId: Long)

    fun joinQueue(token: String, queueInviteCodeDTO: QueueInviteCodeDto): QueueDetailsDto

    fun shakeUser(token: String, queueId: Long)
}
