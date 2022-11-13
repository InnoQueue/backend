package com.innopolis.innoqueue.dao

interface UserQueuesShortForm {
    fun getQueueId(): Long
    fun getQueueName(): String
    fun getColor(): String
    fun getIsActive(): Boolean
}
