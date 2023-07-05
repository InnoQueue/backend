package com.innopolis.innoqueue.domain.user.service

import com.innopolis.innoqueue.domain.fcmtoken.service.FcmTokenService
import com.innopolis.innoqueue.domain.user.dao.UserRepository
import com.innopolis.innoqueue.domain.user.dto.TokenDto
import com.innopolis.innoqueue.domain.user.dto.UpdateUserDto
import com.innopolis.innoqueue.domain.user.dto.UserDto
import com.innopolis.innoqueue.domain.user.model.User
import com.innopolis.innoqueue.util.StringGenerator
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

private const val TOKEN_LENGTH = 64

/**
 * Service for working with the user model
 */
@Service
class UserService(
    private val userRepository: UserRepository,
    private val fcmTokenService: FcmTokenService
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    @Value("\${login.register}")
    private val registerOption: Boolean = true

    /**
     * Return user model by it's token id
     * @param token - user token
     */
    @Transactional
    fun findUserByToken(token: String): User =
        userRepository.findUserByToken(token) ?: throw NoSuchElementException("No such user with token: $token")

    /**
     * Return user model by its id
     * @param userId - user's id
     */
    @Transactional
    fun findUserById(userId: Long): User? = userRepository.findByIdOrNull(userId)

    /**
     * Return user's name by its id
     * @param userId - user's id
     */
    @Transactional
    fun findUserNameById(userId: Long): String? = userRepository.findByIdOrNull(userId)?.name

    /**
     * List user settings
     * @param token - user token
     */
    @Transactional
    fun getUserSettings(token: String): UserDto = this.findUserByToken(token).let {
        UserDto(
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
    @Transactional
    fun updateUserSettings(token: String, settings: UpdateUserDto): UserDto {
        logger.info("user-token=$token")
        val user = this.findUserByToken(token)
        settings.userName?.let {
            require(it.isNotEmpty()) { "Username can't be an empty string" }
            user.name = it
        }
        settings.completed?.let { user.completed = it }
        settings.skipped?.let { user.skipped = it }
        settings.joinedQueue?.let { user.joinedQueue = it }
        settings.freeze?.let { user.freeze = it }
        settings.leftQueue?.let { user.leftQueue = it }
        settings.yourTurn?.let { user.yourTurn = it }
        return userRepository.save(user).let {
            UserDto(
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
    @Transactional
    fun createNewUser(userName: String, fcmToken: String): TokenDto {
        logger.info("Creating new user: userName=$userName, fcmToken=$fcmToken")
        validateUserParameters(userName, fcmToken)
        val token = generateUserToken()
        // TODO remove after adding registration option
        return if (registerOption) {
            logger.info("user-token=$token")
            val userId = saveUser(token, userName, fcmToken)
            TokenDto(token, userId)
        } else {
            val existingUser = userRepository.findAll().toList().firstOrNull { it.name == userName }
            if (existingUser == null) {
                logger.info("user-token=$token")
                val userId = saveUser(token, userName, fcmToken)
                TokenDto(token, userId)
            } else {
                logger.info("Adding new fcmToken to an existing user: userId=${existingUser.id!!}")
                fcmTokenService.saveFcmToken(existingUser.id!!, fcmToken)
                TokenDto(existingUser.token!!, existingUser.id!!)
            }
        }
    }

    private fun validateUserParameters(userName: String, fcmToken: String) {
        require(userName.isNotEmpty()) { "Username can't be an empty string" }
        require(fcmToken.isNotEmpty()) { "fcmToken can't be an empty string" }
    }

    private fun generateUserToken(): String {
        val existingTokens = userRepository.findAll().mapNotNull { it.token }
        val generator = StringGenerator(TOKEN_LENGTH, existingTokens)
        return generator.generateString()
    }

    private fun saveUser(userToken: String, userName: String, userFcmToken: String): Long {
        val userId = userRepository.save(
            User().apply {
                name = userName
                token = userToken
            }
        ).id!!
        fcmTokenService.saveFcmToken(userId, userFcmToken)
        return userId
    }
}
