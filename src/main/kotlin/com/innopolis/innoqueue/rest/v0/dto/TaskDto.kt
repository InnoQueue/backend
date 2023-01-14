package com.innopolis.innoqueue.rest.v0.dto

/**
 * DTO for completing a particular queue
 */
data class TaskDto(
    val taskId: Long,

    val expenses: Long?
)
