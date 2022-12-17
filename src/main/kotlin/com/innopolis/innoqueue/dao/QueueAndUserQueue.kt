package com.innopolis.innoqueue.dao

/**
 * Custom model for the "queue" db table
 */
interface QueueAndUserQueue {
    fun getQueueId(): Long
    fun getQueueName(): String
    fun getQueueColor(): String
    fun getIsImportant(): Boolean
    fun getTrackExpenses(): Boolean
}
