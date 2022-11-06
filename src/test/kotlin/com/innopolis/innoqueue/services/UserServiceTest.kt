package com.innopolis.innoqueue.services

import com.innopolis.innoqueue.models.User
import com.innopolis.innoqueue.models.UserSettings
import com.innopolis.innoqueue.dao.UserRepository
import com.innopolis.innoqueue.dao.UserSettingsRepository
import com.innopolis.innoqueue.testcontainers.PostgresTestContainer
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.jdbc.Sql

class UserServiceTest : PostgresTestContainer() {

    @Autowired
    private lateinit var userService: UserService

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var settingsRepository: UserSettingsRepository

    @Test
    fun `Test createNewUser empty name exception`() {
        // given
        val userName = ""
        val fcmToken = "123"

        // when and then
        assertThrows(IllegalArgumentException::class.java) {
            userService.createNewUser(userName, fcmToken)
        }
    }

    @Test
    fun `Test createNewUser empty fcmToken exception`() {
        // given
        val userName = "user"
        val fcmToken = ""

        // when and then
        assertThrows(IllegalArgumentException::class.java) {
            userService.createNewUser(userName, fcmToken)
        }
    }

    @Test
    fun `Test createNewUser check for existing tokens`() {
        // given
        val userName = "testUser"
        val fcmToken = "123"
        val userRepo = mockk<UserRepository>(relaxed = true)
        every { userRepo.save(any()) } returns User().apply { id = 1L }
        val settingsRepo = mockk<UserSettingsRepository>(relaxed = true)
        every { settingsRepo.save(any()) } returns UserSettings()
        val service = UserService(userRepo, settingsRepo)

        // when
        service.createNewUser(userName, fcmToken)

        // then
        verify(exactly = 1) { userRepo.findAll() }
    }

    @Test
    fun `Test createNewUser token length`() {
        // given
        val userName = "testUser"
        val fcmToken = "123"
        val tokenLength = 64

        // when
        val result = userService.createNewUser(userName, fcmToken)

        // then
        assertEquals(tokenLength, result.token.length)
    }

    @Test
    fun `Test createNewUser user repository called`() {
        // given
        val userName = "testUser"
        val fcmToken = "123"
        val userRepo = mockk<UserRepository>(relaxed = true)
        every { userRepo.save(any()) } returns User().apply { id = 1L }
        val settingsRepo = mockk<UserSettingsRepository>(relaxed = true)
        every { settingsRepo.save(any()) } returns UserSettings()
        val service = UserService(userRepo, settingsRepo)

        // when
        service.createNewUser(userName, fcmToken)

        // then
        verify(exactly = 1) {
            userRepo.save(any())
        }
    }

    @Test
    fun `Test createNewUser settings repository called`() {
        // given
        val userName = "testUser"
        val fcmToken = "123"
        val userRepo = mockk<UserRepository>(relaxed = true)
        every { userRepo.save(any()) } returns User().apply { id = 1L }
        val settingsRepo = mockk<UserSettingsRepository>(relaxed = true)
        every { settingsRepo.save(any()) } returns UserSettings()
        val service = UserService(userRepo, settingsRepo)

        // when
        service.createNewUser(userName, fcmToken)

        // then
        verify(exactly = 1) {
            settingsRepo.save(any())
        }
    }

    @Test
    fun `Test createNewUser user created`() {
        // given
        val userName = "testUser"
        val fcmToken = "123"

        // when
        val result = userService.createNewUser(userName, fcmToken)

        // then
        val users = userRepository.findAll().toList()
        assertEquals(1, users.size)
        assertEquals(userName, users[0].name)
        assertEquals(fcmToken, users[0].fcmToken)
        assertEquals(result.token, users[0].token)
        assertEquals(result.userId, users[0].id)
    }

    @Test
    fun `Test createNewUser user settings created`() {
        // given
        val userName = "testUser"
        val fcmToken = "123"

        // when
        val result = userService.createNewUser(userName, fcmToken)

        // then
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

    @Test
    @Sql("user.sql")
    fun `Test findUserById no such user`() {
        // given
        val id = 2L

        // when
        val user = userService.findUserById(id)

        // then
        assertNull(user)
    }

    @Test
    @Sql("user.sql")
    fun `Test findUserById find user`() {
        // given
        val id = 1L

        // when
        val user = userService.findUserById(id)

        // then
        assertNotNull(user)
        assertEquals(id, user?.id)
    }

    @Test
    @Sql("user.sql")
    fun `Test findUserNameById no such user`() {
        // given
        val id = 2L

        // when
        val userName = userService.findUserNameById(id)

        // then
        assertNull(userName)
    }

    @Test
    @Sql("user.sql")
    fun `Test findUserNameById find user`() {
        // given
        val id = 1L

        // when
        val userName = userService.findUserNameById(id)

        // then
        assertNotNull(userName)
        assertEquals("user name", userName)
    }

    @Test
    @Sql("user.sql")
    fun `Test findUserByToken no such user exception`() {
        // given
        val token = "123"

        // when and then
        assertThrows(NoSuchElementException::class.java) {
            userService.findUserByToken(token)
        }
    }

    @Test
    @Sql("user.sql")
    fun `Test findUserByToken find user`() {
        // given
        val token = "token"

        // when
        val user = userService.findUserByToken(token)

        // then
        assertNotNull(user)
        assertEquals(token, user.token)
    }
}
