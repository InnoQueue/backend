package com.innopolis.innoqueue.service

import com.innopolis.innoqueue.controller.dto.TokenDTO
import com.innopolis.innoqueue.model.User
import com.innopolis.innoqueue.model.UserSetting
import com.innopolis.innoqueue.repository.UserRepository
import com.innopolis.innoqueue.repository.UserSettingsRepository
import com.innopolis.innoqueue.utils.StringGenerator
import org.springframework.stereotype.Service

@Service
class UserService(
    private val userRepository: UserRepository,
    private val settingsRepository: UserSettingsRepository,
) {
    private val tokenLength = 16

    fun getUserByToken(token: String): User {
        return userRepository.findAll().firstOrNull { user -> user.token == token }
            ?: throw NoSuchElementException("No such user with token: $token")
    }

    fun generateUserToken(): TokenDTO {
        val existingTokens = userRepository.findAll().map { it.token }
        val generator = StringGenerator(tokenLength)
        while (true) {
            val randomString = generator.generateString()
            if (!existingTokens.contains(randomString)) {
                val newUser = User()
                newUser.token = randomString
                newUser.name = "username"
                userRepository.save(newUser)
                val settings = UserSetting()
                settings.user = newUser
                settingsRepository.save(settings)
                return TokenDTO(randomString)
            }
        }
    }
}