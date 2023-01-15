package com.innopolis.innoqueue.domain.userqueue.model

import java.time.LocalDateTime

/**
 * Custom model for the "user_queue" db table
 */
interface UserQueuesShortForm {
    fun getQueueId(): Long
    fun getQueueName(): String
    fun getColor(): String
    fun getIsActive(): Boolean
    fun getOnDutyUserName(): String
    fun getDateJoined(): LocalDateTime
}
