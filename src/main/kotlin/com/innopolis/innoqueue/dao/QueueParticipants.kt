package com.innopolis.innoqueue.dao

interface QueueParticipants {
    fun getUserId(): Long
    fun getUserName(): String
    fun getExpenses(): Double
    fun getIsActive(): Boolean
}
