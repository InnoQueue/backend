package com.innopolis.innoqueue.domain.user.service

import com.innopolis.innoqueue.domain.user.dto.TokenDto
import com.innopolis.innoqueue.domain.user.dto.UpdateUserDto
import com.innopolis.innoqueue.domain.user.dto.UserDto
import com.innopolis.innoqueue.domain.user.model.User

interface UserService {
    fun findUserByToken(token: String): User

    fun findUserById(userId: Long): User?

    fun findUserNameById(userId: Long): String?

    fun getUserSettings(token: String): UserDto

    fun updateUserSettings(token: String, settings: UpdateUserDto): UserDto

    fun createNewUser(userName: String, fcmToken: String): TokenDto
}
