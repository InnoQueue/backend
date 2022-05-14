package com.innopolis.innoqueue.service

import com.innopolis.innoqueue.controller.dto.TokenDTO
import com.innopolis.innoqueue.model.User
import com.innopolis.innoqueue.model.UserSetting
import com.innopolis.innoqueue.repository.UserRepository
import com.innopolis.innoqueue.repository.UserSettingsRepository
import com.innopolis.innoqueue.utils.StringGenerator
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class UserService(
    private val userRepository: UserRepository,
    private val settingsRepository: UserSettingsRepository,
) {
    private val tokenLength = 64

    fun getUserByToken(token: String): User {
        return userRepository.findAll().firstOrNull { user -> user.token == token }
            ?: throw NoSuchElementException("No such user with token: $token")
    }

    fun getUserById(userId: Long): User? {
        return userRepository.findByIdOrNull(userId)
    }

    fun generateUserToken(userName: String, fcmToken: String): TokenDTO {
        // TODO remove after demo presentation
        val reservedUsers: List<Triple<String, Long, String>> = listOf(
            Triple("admin", 1, "11111"),
//            Triple("Emil", 2, "22222"),
//            Triple("Roman", 3, "33333"),
//            Triple("Timur", 4, "44444"),
//            Triple("Ivan", 5, "55555")
        )
        for (r in reservedUsers) {
            val (n, i, t) = r
            if (userName == n) {
                val u = userRepository.findByIdOrNull(i)!!
                u.fcmToken = fcmToken
                userRepository.save(u)
                return TokenDTO(t, i)
            }
        }

        if (userName.isEmpty()) {
            throw IllegalArgumentException("Username can't be an empty string")
        }
        val existingTokens = userRepository.findAll().map { it.token }
        val generator = StringGenerator(tokenLength)
        while (true) {
            val randomString = generator.generateString()
            if (!existingTokens.contains(randomString)) {
                val savedUser = createNewUser(randomString, userName, fcmToken)
                return TokenDTO(randomString, savedUser.id!!)
            }
        }
    }

    private fun createNewUser(token: String, userName: String, fcmToken: String): User {
        val newUser = User()
        newUser.name = userName
        newUser.token = token
        newUser.fcmToken = fcmToken
        val savedUser = userRepository.save(newUser)
        val settings = UserSetting()
        settings.user = newUser
        settingsRepository.save(settings)
        return savedUser
    }
}
