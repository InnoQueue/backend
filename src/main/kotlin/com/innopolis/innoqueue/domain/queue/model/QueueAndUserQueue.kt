package com.innopolis.innoqueue.domain.queue.model

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
