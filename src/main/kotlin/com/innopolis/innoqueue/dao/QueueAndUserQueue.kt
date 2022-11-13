package com.innopolis.innoqueue.dao

interface QueueAndUserQueue {
    fun getQueueId(): Long
    fun getQueueName(): String
    fun getQueueColor(): String
    fun getIsImportant(): Boolean
    fun getTrackExpenses(): Boolean
}
