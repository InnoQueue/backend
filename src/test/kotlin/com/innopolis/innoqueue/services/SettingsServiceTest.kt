package com.innopolis.innoqueue.services

import com.innopolis.innoqueue.dao.UserRepository
import com.innopolis.innoqueue.dao.UserSettingsRepository
import com.innopolis.innoqueue.dto.SettingsDTO
import com.innopolis.innoqueue.models.User
import com.innopolis.innoqueue.models.UserSettings
import com.innopolis.innoqueue.testcontainers.PostgresTestContainer
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.jdbc.Sql

class SettingsServiceTest : PostgresTestContainer() {

    @Autowired
    private lateinit var settingsService: SettingsService

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var settingsRepository: UserSettingsRepository

    @Test
    @Sql("user.sql", "settings.sql")
    fun `Test getSettings`() {
        // given
        val token = "token"

        // when
        val settings = settingsService.getSettings(token)

        // then
        assertEquals("user name", settings.userName)
        assertEquals(true, settings.completed)
        assertEquals(false, settings.skipped)
        assertEquals(true, settings.joinedQueue)
        assertEquals(false, settings.freeze)
        assertEquals(true, settings.leftQueue)
        assertEquals(false, settings.yourTurn)
    }

    @Test
    @Sql("user.sql", "settings.sql")
    fun `Test updateSettings empty userName exception`() {
        // given
        val token = "token"
        val settingsDTO = SettingsDTO(
            userName = "",
            completed = null,
            skipped = null,
            joinedQueue = null,
            freeze = null,
            leftQueue = null,
            yourTurn = null
        )

        // when and then
        assertThrows(IllegalArgumentException::class.java) {
            settingsService.updateSettings(token, settingsDTO)
        }
    }

    @Test
    @Sql("user.sql", "settings.sql")
    fun `Test updateSettings no changes in userName and no user repo save calls`() {
        // given
        val token = "token"
        val settingsDTO = SettingsDTO(
            userName = null,
            completed = true,
            skipped = true,
            joinedQueue = true,
            freeze = true,
            leftQueue = true,
            yourTurn = true
        )
        val userService = mockk<UserService>(relaxed = true)
        every { userService.findUserByToken(token) } returns User().apply {
            id = 1L
            name = "user name"
            settings = UserSettings()
        }
        val userRepo = mockk<UserRepository>(relaxed = true)
        val settingsRepo = mockk<UserSettingsRepository>(relaxed = true)
        every { settingsRepo.save(any()) } returns UserSettings().apply {
            completed = true
            skipped = true
            joinedQueue = true
            freeze = true
            leftQueue = true
            yourTurn = true
        }
        val service = SettingsService(userService, userRepo, settingsRepo)

        // when
        service.updateSettings(token, settingsDTO)

        // then
        verify(exactly = 0) { userRepo.save(any()) }
    }

    @Test
    fun `Test updateSettings change userName and user repo save called`() {
        // given
        val token = "token"
        val settingsDTO = SettingsDTO(
            userName = "new user",
            completed = null,
            skipped = null,
            joinedQueue = null,
            freeze = null,
            leftQueue = null,
            yourTurn = null
        )
        val userService = mockk<UserService>(relaxed = true)
        every { userService.findUserByToken(token) } returns User().apply {
            id = 1L
            name = "user name"
            settings = UserSettings()
        }
        val userRepo = mockk<UserRepository>(relaxed = true)
        every { userRepo.save(any()) } returns User().apply {
            id = 1L
            name = "new user"
            settings = UserSettings()
        }
        val settingsRepo = mockk<UserSettingsRepository>(relaxed = true)
        every { settingsRepo.save(any()) } returns UserSettings().apply {
            completed = true
            skipped = true
            joinedQueue = true
            freeze = true
            leftQueue = true
            yourTurn = true
        }
        val service = SettingsService(userService, userRepo, settingsRepo)

        // when
        service.updateSettings(token, settingsDTO)

        // then
        verify(exactly = 1) { userRepo.save(any()) }
    }

    @Test
    fun `Test updateSettings no updates and no DB calls`() {
        // given
        val token = "token"
        val settingsDTO = SettingsDTO(
            userName = null,
            completed = null,
            skipped = null,
            joinedQueue = null,
            freeze = null,
            leftQueue = null,
            yourTurn = null
        )
        val userService = mockk<UserService>(relaxed = true)
        every { userService.findUserByToken(token) } returns User().apply {
            id = 1L
            name = "user name"
            settings = UserSettings()
        }
        val userRepo = mockk<UserRepository>(relaxed = true)
        val settingsRepo = mockk<UserSettingsRepository>(relaxed = true)
        val service = SettingsService(userService, userRepo, settingsRepo)

        // when
        service.updateSettings(token, settingsDTO)

        // then
        verify(exactly = 0) { userRepo.save(any()) }
        verify(exactly = 0) { settingsRepo.save(any()) }
    }

    @Test
    @Sql("user.sql", "settings.sql")
    fun `Test updateSettings no updates and no changes in DB`() {
        // given
        val token = "token"
        val settingsDTO = SettingsDTO(
            userName = null,
            completed = null,
            skipped = null,
            joinedQueue = null,
            freeze = null,
            leftQueue = null,
            yourTurn = null
        )

        // when
        val response = settingsService.updateSettings(token, settingsDTO)
        val userModel = userRepository.findAll().first()
        val userSettingsModel = settingsRepository.findAll().first()

        // then
        // response model
        assertEquals("user name", response.userName)
        assertEquals(true, response.completed)
        assertEquals(false, response.skipped)
        assertEquals(true, response.joinedQueue)
        assertEquals(false, response.freeze)
        assertEquals(true, response.leftQueue)
        assertEquals(false, response.yourTurn)

        // database
        assertEquals("user name", userModel.name)
        assertEquals(true, userSettingsModel.completed)
        assertEquals(false, userSettingsModel.skipped)
        assertEquals(true, userSettingsModel.joinedQueue)
        assertEquals(false, userSettingsModel.freeze)
        assertEquals(true, userSettingsModel.leftQueue)
        assertEquals(false, userSettingsModel.yourTurn)
    }

    @Test
    fun `Test updateSettings change settings and settings repo save called`() {
        // given
        val token = "token"
        val settingsDTO = SettingsDTO(
            userName = null,
            completed = true,
            skipped = true,
            joinedQueue = true,
            freeze = true,
            leftQueue = true,
            yourTurn = true
        )
        val userService = mockk<UserService>(relaxed = true)
        every { userService.findUserByToken(token) } returns User().apply {
            id = 1L
            name = "user name"
            settings = UserSettings()
        }
        val userRepo = mockk<UserRepository>(relaxed = true)
        val settingsRepo = mockk<UserSettingsRepository>(relaxed = true)
        every { settingsRepo.save(any()) } returns UserSettings().apply {
            completed = true
            skipped = true
            joinedQueue = true
            freeze = true
            leftQueue = true
            yourTurn = true
        }
        val service = SettingsService(userService, userRepo, settingsRepo)

        // when
        service.updateSettings(token, settingsDTO)

        // then
        verify(exactly = 1) { settingsRepo.save(any()) }
    }

    @Test
    @Sql("user.sql", "settings.sql")
    fun `Test updateSettings change userName`() {
        // given
        val token = "token"
        val userName = "new user name"
        val settingsDTO = SettingsDTO(
            userName = "new user name",
            completed = null,
            skipped = null,
            joinedQueue = null,
            freeze = null,
            leftQueue = null,
            yourTurn = null
        )

        // when
        val response = settingsService.updateSettings(token, settingsDTO)
        val userModel = userRepository.findAll().first()

        // then
        assertEquals(userName, userModel.name)
        assertEquals(userName, response.userName)
        assertEquals(true, response.completed)
        assertEquals(false, response.skipped)
        assertEquals(true, response.joinedQueue)
        assertEquals(false, response.freeze)
        assertEquals(true, response.leftQueue)
        assertEquals(false, response.yourTurn)
    }

    @Test
    @Sql("user.sql", "settings.sql")
    fun `Test updateSettings change completed`() {
        // given
        val token = "token"
        val completed = false
        val skipped = false
        val joinedQueue = true
        val freeze = false
        val leftQueue = true
        val yourTurn = false
        val settingsDTO = SettingsDTO(
            userName = null,
            completed = completed,
            skipped = null,
            joinedQueue = null,
            freeze = null,
            leftQueue = null,
            yourTurn = null
        )

        // when
        val response = settingsService.updateSettings(token, settingsDTO)
        val userSettingsModel = settingsRepository.findAll().first()

        // then
        // response model
        assertEquals("user name", response.userName)
        assertEquals(completed, response.completed)
        assertEquals(skipped, response.skipped)
        assertEquals(joinedQueue, response.joinedQueue)
        assertEquals(freeze, response.freeze)
        assertEquals(leftQueue, response.leftQueue)
        assertEquals(yourTurn, response.yourTurn)

        // database
        assertEquals(completed, userSettingsModel.completed)
        assertEquals(skipped, userSettingsModel.skipped)
        assertEquals(joinedQueue, userSettingsModel.joinedQueue)
        assertEquals(freeze, userSettingsModel.freeze)
        assertEquals(leftQueue, userSettingsModel.leftQueue)
        assertEquals(yourTurn, userSettingsModel.yourTurn)
    }

    @Test
    @Sql("user.sql", "settings.sql")
    fun `Test updateSettings change skipped`() {
        // given
        val token = "token"
        val completed = true
        val skipped = true
        val joinedQueue = true
        val freeze = false
        val leftQueue = true
        val yourTurn = false
        val settingsDTO = SettingsDTO(
            userName = null,
            completed = null,
            skipped = skipped,
            joinedQueue = null,
            freeze = null,
            leftQueue = null,
            yourTurn = null
        )

        // when
        val response = settingsService.updateSettings(token, settingsDTO)
        val userSettingsModel = settingsRepository.findAll().first()

        // then
        // response model
        assertEquals("user name", response.userName)
        assertEquals(completed, response.completed)
        assertEquals(skipped, response.skipped)
        assertEquals(joinedQueue, response.joinedQueue)
        assertEquals(freeze, response.freeze)
        assertEquals(leftQueue, response.leftQueue)
        assertEquals(yourTurn, response.yourTurn)

        // database
        assertEquals(completed, userSettingsModel.completed)
        assertEquals(skipped, userSettingsModel.skipped)
        assertEquals(joinedQueue, userSettingsModel.joinedQueue)
        assertEquals(freeze, userSettingsModel.freeze)
        assertEquals(leftQueue, userSettingsModel.leftQueue)
        assertEquals(yourTurn, userSettingsModel.yourTurn)
    }

    @Test
    @Sql("user.sql", "settings.sql")
    fun `Test updateSettings change joinedQueue`() {
        // given
        val token = "token"
        val completed = true
        val skipped = false
        val joinedQueue = false
        val freeze = false
        val leftQueue = true
        val yourTurn = false
        val settingsDTO = SettingsDTO(
            userName = null,
            completed = null,
            skipped = null,
            joinedQueue = joinedQueue,
            freeze = null,
            leftQueue = null,
            yourTurn = null
        )

        // when
        val response = settingsService.updateSettings(token, settingsDTO)
        val userSettingsModel = settingsRepository.findAll().first()

        // then
        // response model
        assertEquals("user name", response.userName)
        assertEquals(completed, response.completed)
        assertEquals(skipped, response.skipped)
        assertEquals(joinedQueue, response.joinedQueue)
        assertEquals(freeze, response.freeze)
        assertEquals(leftQueue, response.leftQueue)
        assertEquals(yourTurn, response.yourTurn)

        // database
        assertEquals(completed, userSettingsModel.completed)
        assertEquals(skipped, userSettingsModel.skipped)
        assertEquals(joinedQueue, userSettingsModel.joinedQueue)
        assertEquals(freeze, userSettingsModel.freeze)
        assertEquals(leftQueue, userSettingsModel.leftQueue)
        assertEquals(yourTurn, userSettingsModel.yourTurn)
    }

    @Test
    @Sql("user.sql", "settings.sql")
    fun `Test updateSettings change freeze`() {
        // given
        val token = "token"
        val completed = true
        val skipped = false
        val joinedQueue = true
        val freeze = true
        val leftQueue = true
        val yourTurn = false
        val settingsDTO = SettingsDTO(
            userName = null,
            completed = null,
            skipped = null,
            joinedQueue = null,
            freeze = freeze,
            leftQueue = null,
            yourTurn = null
        )

        // when
        val response = settingsService.updateSettings(token, settingsDTO)
        val userSettingsModel = settingsRepository.findAll().first()

        // then
        // response model
        assertEquals("user name", response.userName)
        assertEquals(completed, response.completed)
        assertEquals(skipped, response.skipped)
        assertEquals(joinedQueue, response.joinedQueue)
        assertEquals(freeze, response.freeze)
        assertEquals(leftQueue, response.leftQueue)
        assertEquals(yourTurn, response.yourTurn)

        // database
        assertEquals(completed, userSettingsModel.completed)
        assertEquals(skipped, userSettingsModel.skipped)
        assertEquals(joinedQueue, userSettingsModel.joinedQueue)
        assertEquals(freeze, userSettingsModel.freeze)
        assertEquals(leftQueue, userSettingsModel.leftQueue)
        assertEquals(yourTurn, userSettingsModel.yourTurn)
    }

    @Test
    @Sql("user.sql", "settings.sql")
    fun `Test updateSettings change leftQueue`() {
        // given
        val token = "token"
        val completed = true
        val skipped = false
        val joinedQueue = true
        val freeze = false
        val leftQueue = false
        val yourTurn = false
        val settingsDTO = SettingsDTO(
            userName = null,
            completed = null,
            skipped = null,
            joinedQueue = null,
            freeze = null,
            leftQueue = leftQueue,
            yourTurn = null
        )

        // when
        val response = settingsService.updateSettings(token, settingsDTO)
        val userSettingsModel = settingsRepository.findAll().first()

        // then
        // response model
        assertEquals("user name", response.userName)
        assertEquals(completed, response.completed)
        assertEquals(skipped, response.skipped)
        assertEquals(joinedQueue, response.joinedQueue)
        assertEquals(freeze, response.freeze)
        assertEquals(leftQueue, response.leftQueue)
        assertEquals(yourTurn, response.yourTurn)

        // database
        assertEquals(completed, userSettingsModel.completed)
        assertEquals(skipped, userSettingsModel.skipped)
        assertEquals(joinedQueue, userSettingsModel.joinedQueue)
        assertEquals(freeze, userSettingsModel.freeze)
        assertEquals(leftQueue, userSettingsModel.leftQueue)
        assertEquals(yourTurn, userSettingsModel.yourTurn)
    }

    @Test
    @Sql("user.sql", "settings.sql")
    fun `Test updateSettings change yourTurn`() {
        // given
        val token = "token"
        val completed = true
        val skipped = false
        val joinedQueue = true
        val freeze = false
        val leftQueue = true
        val yourTurn = true
        val settingsDTO = SettingsDTO(
            userName = null,
            completed = null,
            skipped = null,
            joinedQueue = null,
            freeze = null,
            leftQueue = null,
            yourTurn = yourTurn
        )

        // when
        val response = settingsService.updateSettings(token, settingsDTO)
        val userSettingsModel = settingsRepository.findAll().first()

        // then
        // response model
        assertEquals("user name", response.userName)
        assertEquals(completed, response.completed)
        assertEquals(skipped, response.skipped)
        assertEquals(joinedQueue, response.joinedQueue)
        assertEquals(freeze, response.freeze)
        assertEquals(leftQueue, response.leftQueue)
        assertEquals(yourTurn, response.yourTurn)

        // database
        assertEquals(completed, userSettingsModel.completed)
        assertEquals(skipped, userSettingsModel.skipped)
        assertEquals(joinedQueue, userSettingsModel.joinedQueue)
        assertEquals(freeze, userSettingsModel.freeze)
        assertEquals(leftQueue, userSettingsModel.leftQueue)
        assertEquals(yourTurn, userSettingsModel.yourTurn)
    }

    @Test
    @Sql("user.sql", "settings.sql")
    fun `Test updateSettings update all settings`() {
        // given
        val token = "token"
        val completed = false
        val skipped = false
        val joinedQueue = false
        val freeze = false
        val leftQueue = false
        val yourTurn = false
        val settingsDTO = SettingsDTO(
            userName = null,
            completed = completed,
            skipped = skipped,
            joinedQueue = joinedQueue,
            freeze = freeze,
            leftQueue = leftQueue,
            yourTurn = yourTurn
        )

        // when
        val response = settingsService.updateSettings(token, settingsDTO)
        val userSettingsModel = settingsRepository.findAll().first()

        // then
        // response model
        assertEquals("user name", response.userName)
        assertEquals(completed, response.completed)
        assertEquals(skipped, response.skipped)
        assertEquals(joinedQueue, response.joinedQueue)
        assertEquals(freeze, response.freeze)
        assertEquals(leftQueue, response.leftQueue)
        assertEquals(yourTurn, response.yourTurn)

        // database
        assertEquals(completed, userSettingsModel.completed)
        assertEquals(skipped, userSettingsModel.skipped)
        assertEquals(joinedQueue, userSettingsModel.joinedQueue)
        assertEquals(freeze, userSettingsModel.freeze)
        assertEquals(leftQueue, userSettingsModel.leftQueue)
        assertEquals(yourTurn, userSettingsModel.yourTurn)
    }

    @Test
    @Sql("user.sql", "settings.sql")
    fun `Test updateSettings update all settings and userName`() {
        // given
        val token = "token"
        val userName = "new user name"
        val completed = false
        val skipped = false
        val joinedQueue = false
        val freeze = false
        val leftQueue = false
        val yourTurn = false
        val settingsDTO = SettingsDTO(
            userName = userName,
            completed = completed,
            skipped = skipped,
            joinedQueue = joinedQueue,
            freeze = freeze,
            leftQueue = leftQueue,
            yourTurn = yourTurn
        )

        // when
        val response = settingsService.updateSettings(token, settingsDTO)
        val userModel = userRepository.findAll().first()
        val userSettingsModel = settingsRepository.findAll().first()

        // then
        // response model
        assertEquals(userName, response.userName)
        assertEquals(completed, response.completed)
        assertEquals(skipped, response.skipped)
        assertEquals(joinedQueue, response.joinedQueue)
        assertEquals(freeze, response.freeze)
        assertEquals(leftQueue, response.leftQueue)
        assertEquals(yourTurn, response.yourTurn)

        // database
        assertEquals(userName, userModel.name)
        assertEquals(completed, userSettingsModel.completed)
        assertEquals(skipped, userSettingsModel.skipped)
        assertEquals(joinedQueue, userSettingsModel.joinedQueue)
        assertEquals(freeze, userSettingsModel.freeze)
        assertEquals(leftQueue, userSettingsModel.leftQueue)
        assertEquals(yourTurn, userSettingsModel.yourTurn)
    }
}
