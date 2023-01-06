package com.innopolis.innoqueue.domain.userqueue.model

/**
 * Custom model for the "user_queue" db table
 */
interface QueueParticipants {
    fun getUserId(): Long
    fun getUserName(): String
    fun getExpenses(): Double
    fun getIsActive(): Boolean
}
