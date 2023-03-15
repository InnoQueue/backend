package com.innopolis.innoqueue.rest.v1.dto

/**
 * DTO request for creating new user account
 */
data class NewUserDto(
    val userName: String,
    val fcmToken: String
)
