package com.innopolis.innoqueue.rest.v0.dto

/**
 * DTO request for creating new user account
 */
data class NewUserDto(
    val userName: String,
    val fcmToken: String
)
