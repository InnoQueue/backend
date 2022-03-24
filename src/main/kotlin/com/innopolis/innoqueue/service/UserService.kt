package com.innopolis.innoqueue.service

import com.innopolis.innoqueue.model.User
import com.innopolis.innoqueue.repository.UserRepository
import org.springframework.stereotype.Service

@Service
class UserService(private val userRepository: UserRepository) {
    fun getUserByToken(token: Long): User {
        return userRepository.findAll().firstOrNull { user -> user.token == token }
            ?: throw NoSuchElementException("No such user with token: $token")
    }
}