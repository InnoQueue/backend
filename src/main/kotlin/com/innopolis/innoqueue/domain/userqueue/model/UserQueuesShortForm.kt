package com.innopolis.innoqueue.domain.userqueue.model

/**
 * Custom model for the "user_queue" db table
 */
interface UserQueuesShortForm {
    fun getQueueId(): Long
    fun getQueueName(): String
    fun getColor(): String
    fun getIsActive(): Boolean
    fun getUserName(): String
}
