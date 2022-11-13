package com.innopolis.innoqueue.dao

interface UserQueueAndQueue {
    fun getUserId(): Long
    fun getQueueId(): Long
    fun getCurrentUserId(): Long
    fun getTrackExpenses(): Boolean
    fun getIsActive(): Boolean
    fun getSkips(): Long
    fun getExpenses(): Double
}
