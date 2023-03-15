package com.innopolis.innoqueue.domain.userqueue.model

/**
 * Custom model for the "user_queue" db table
 */
interface UserQueueAndQueue {
    fun getUserId(): Long

    fun getQueueId(): Long

    fun getCurrentUserId(): Long

    fun getTrackExpenses(): Boolean

    fun getIsActive(): Boolean

    fun getProgress(): Long

    fun getCompletes(): Long

    fun getSkips(): Long

    fun getExpenses(): Double
}
