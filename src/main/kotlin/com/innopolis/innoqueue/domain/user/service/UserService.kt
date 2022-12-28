package com.innopolis.innoqueue.domain.user.service

import com.innopolis.innoqueue.domain.user.dao.UserRepository
import com.innopolis.innoqueue.domain.user.dto.TokenDTO
import com.innopolis.innoqueue.domain.user.dto.UpdateUserDTO
import com.innopolis.innoqueue.domain.user.dto.UserDTO
import com.innopolis.innoqueue.domain.user.model.User
import com.innopolis.innoqueue.util.StringGenerator
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
    private val environment: Environment
) {
    /**
     * Return user model by it's token id
     * @param token - user token
     */
    fun findUserByToken(token: String): User =
        userRepository.findUserByToken(token) ?: throw NoSuchElementException("No such user with token: $token")

    /**
     * Return user model by its id
     * @param userId - user's id
     */
    fun findUserById(userId: Long): User? = userRepository.findByIdOrNull(userId)

    /**
     * Return user's name by its id
     * @param userId - user's id
     */
    fun findUserNameById(userId: Long): String? = userRepository.findByIdOrNull(userId)?.name

    /**
     * List user settings
     * @param token - user token
     */
    fun getUserSettings(token: String): UserDTO = this.findUserByToken(token).let {
        UserDTO(
            it.name!!,
            it.completed!!,
            it.skipped!!,
            it.joinedQueue!!,
            it.freeze!!,
            it.leftQueue!!,
            it.yourTurn!!
        )
    }

    /**
     * Update user settings
     * @param token - user token
     */
    fun updateUserSettings(token: String, settings: UpdateUserDTO): UserDTO {
        val user = this.findUserByToken(token)
        settings.userName?.let {
            if (it.isEmpty()) {
                throw IllegalArgumentException("Username can't be an empty string")
            }
            user.name = it
        }
        settings.completed?.let { user.completed = it }
        settings.skipped?.let { user.skipped = it }
        settings.joinedQueue?.let { user.joinedQueue = it }
        settings.freeze?.let { user.freeze = it }
        settings.leftQueue?.let { user.leftQueue = it }
        settings.yourTurn?.let { user.yourTurn = it }
        return userRepository.save(user).let {
            UserDTO(
                it.name!!,
                it.completed!!,
                it.skipped!!,
                it.joinedQueue!!,
                it.freeze!!,
                it.leftQueue!!,
                it.yourTurn!!
            )
        }
    }

    /**
     * Create new user model
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

    private fun saveUser(userToken: String, userName: String, userFcmToken: String): Long =
        userRepository.save(
            User()
                .apply {
                    name = userName
                    token = userToken
                    fcmToken = userFcmToken
                }
        ).id!!
}
