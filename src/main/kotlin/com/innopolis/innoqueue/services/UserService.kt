package com.innopolis.innoqueue.services

import com.innopolis.innoqueue.dao.UserRepository
import com.innopolis.innoqueue.dao.UserSettingsRepository
import com.innopolis.innoqueue.models.User
import com.innopolis.innoqueue.models.UserSettings
import com.innopolis.innoqueue.rest.v1.dto.TokenDTO
import com.innopolis.innoqueue.utils.StringGenerator
import org.springframework.core.env.Environment
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

private const val TOKEN_LENGTH = 64

/**
 * Service for working with the user model
 */
@Service
class UserService(
        private val userRepository: UserRepository,
        private val settingsRepository: UserSettingsRepository,
        private val environment: Environment
) {
    /**
     * Creates new user model
     * @param userName - user's name
     * @param fcmToken - device Firebase token
     */
    fun createNewUser(userName: String, fcmToken: String): TokenDTO {
        validateUserParameters(userName, fcmToken)
        val token = generateUserToken()
        // TODO remove after adding registration option
        return if (!environment.activeProfiles.contains("dev")) {
            val userId = saveUser(token, userName, fcmToken)
            TokenDTO(token, userId)
        } else {
            val existingUser = userRepository.findAll().toList().firstOrNull { it.name == userName }
            if (existingUser == null) {
                val userId = saveUser(token, userName, fcmToken)
                TokenDTO(token, userId)
            } else {
                existingUser.fcmToken = fcmToken
                userRepository.save(existingUser)
                TokenDTO(existingUser.token!!, existingUser.id!!)
            }
        }
    }

    /**
     * Returns user model by it's id
     * @param userId - user's id
     */
    fun findUserById(userId: Long): User? = userRepository.findByIdOrNull(userId)

    /**
     * Returns user's name by it's id
     * @param userId - user's id
     */
    fun findUserNameById(userId: Long): String? = userRepository.findByIdOrNull(userId)?.name

    /**
     * Returns user model by it's token id
     * @param token - user token
     */
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
