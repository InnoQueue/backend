package com.innopolis.innoqueue.services

import com.innopolis.innoqueue.dao.UserRepository
import com.innopolis.innoqueue.dao.UserSettingsRepository
import com.innopolis.innoqueue.models.User
import com.innopolis.innoqueue.models.UserSettings
import com.innopolis.innoqueue.rest.v1.dto.TokenDTO
import com.innopolis.innoqueue.utils.StringGenerator
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

private const val TOKEN_LENGTH = 64

@Service
class UserService(
    private val userRepository: UserRepository,
    private val settingsRepository: UserSettingsRepository,
) {
    fun createNewUser(userName: String, fcmToken: String): TokenDTO {
        validateUserParameters(userName, fcmToken)
        val token = generateUserToken()
        val userId = saveUser(token, userName, fcmToken)
        return TokenDTO(token, userId)
    }

    fun findUserById(userId: Long): User? = userRepository.findByIdOrNull(userId)

    fun findUserNameById(userId: Long): String? = userRepository.findByIdOrNull(userId)?.name

    fun findUserByToken(token: String): User =
        userRepository.findUserByToken(token) ?: throw NoSuchElementException("No such user with token: $token")

    private fun validateUserParameters(userName: String, fcmToken: String) {
        if (userName.isEmpty()) {
            throw IllegalArgumentException("Username can't be an empty string")
        }
        if (fcmToken.isEmpty()) {
            throw IllegalArgumentException("fcmToken can't be an empty string")
        }
    }

    private fun generateUserToken(): String {
        val existingTokens = userRepository.findAll().mapNotNull { it.token }
        val generator = StringGenerator(TOKEN_LENGTH, existingTokens)
        return generator.generateString()
    }

    private fun saveUser(token: String, userName: String, fcmToken: String): Long {
        val newUser = User()
        newUser.name = userName
        newUser.token = token
        newUser.fcmToken = fcmToken
        val savedUser = userRepository.save(newUser)
        val settings = UserSettings()
        settings.user = newUser
        settingsRepository.save(settings)
        return savedUser.id!!
    }
}
