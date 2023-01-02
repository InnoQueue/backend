package com.innopolis.innoqueue.rest.v1.dto

/**
 * DTO for completing a particular queue
 */
data class TaskDTO(
    val taskId: Long,

    val expenses: Long?
)
