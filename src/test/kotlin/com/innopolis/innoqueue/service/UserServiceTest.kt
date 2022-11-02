package com.innopolis.innoqueue.service

import com.innopolis.innoqueue.repository.UserRepository
import com.innopolis.innoqueue.repository.UserSettingsRepository
import com.innopolis.innoqueue.testcontainers.PostgresTestContainer
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class UserServiceTest : PostgresTestContainer() {

    @Autowired
    private lateinit var userService: UserService

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var settingsRepository: UserSettingsRepository

    @Test
    fun `Test generateUserToken empty name exception`() {
        val userName = ""
        val fcmToken = ""
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            userService.generateUserToken(userName, fcmToken)
        }
    }

    @Test
    fun `Test generateUserToken token length`() {
        val userName = "testUser"
        val fcmToken = "123"
        val tokenLength = 64
        val result = userService.generateUserToken(userName, fcmToken)
        assertEquals(tokenLength, result.token.length)
    }

    @Test
    fun `Test generateUserToken user created`() {
        val userName = "testUser"
        val fcmToken = "123"
        val result = userService.generateUserToken(userName, fcmToken)
        val users = userRepository.findAll().toList()
        assertEquals(1, users.size)
        assertEquals(userName, users[0].name)
        assertEquals(fcmToken, users[0].fcmToken)
        assertEquals(result.token, users[0].token)
        assertEquals(result.userId, users[0].id)
    }

    @Test
    fun `Test generateUserToken user settings created`() {
        val userName = "testUser"
        val fcmToken = "123"
        val result = userService.generateUserToken(userName, fcmToken)
        val settings = settingsRepository.findAll().toList()
        assertEquals(1, settings.size)
        assertEquals(result.userId, settings[0].user?.id)
        assertEquals(true, settings[0].completed)
        assertEquals(true, settings[0].skipped)
        assertEquals(true, settings[0].joinedQueue)
        assertEquals(true, settings[0].freeze)
        assertEquals(true, settings[0].leftQueue)
        assertEquals(true, settings[0].yourTurn)
    }
}
