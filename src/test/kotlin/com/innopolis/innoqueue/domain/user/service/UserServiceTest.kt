package com.innopolis.innoqueue.domain.user.service

import com.innopolis.innoqueue.domain.fcmtoken.dao.FcmTokenRepository
import com.innopolis.innoqueue.domain.fcmtoken.service.FcmTokenService
import com.innopolis.innoqueue.domain.user.dao.UserRepository
import com.innopolis.innoqueue.domain.user.dto.UpdateUserDto
import com.innopolis.innoqueue.domain.user.model.User
import com.innopolis.innoqueue.testcontainer.PostgresTestContainer
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.env.Environment
import org.springframework.test.context.jdbc.Sql

@Suppress("LargeClass")
class UserServiceTest : PostgresTestContainer() {

    @Autowired
    private lateinit var userService: UserService

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var fcmTokenRepository: FcmTokenRepository

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
        val fcmService = mockk<FcmTokenService>(relaxed = true)
        every { fcmService.saveFcmToken(any(), any()) } returns Unit
        val service = UserService(userRepo, fcmService)

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
        val fcmService = mockk<FcmTokenService>(relaxed = true)
        every { fcmService.saveFcmToken(any(), any()) } returns Unit
        val service = UserService(userRepo, fcmService)

        // when
        service.createNewUser(userName, fcmToken)

        // then
        verify(exactly = 1) {
            userRepo.save(any())
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
        with(users[0]) {
            assertEquals(userName, name)
            assertEquals(result.token, token)
            assertEquals(result.userId, id)
            assertEquals(true, completed)
            assertEquals(true, skipped)
            assertEquals(true, joinedQueue)
            assertEquals(true, freeze)
            assertEquals(true, leftQueue)
            assertEquals(true, yourTurn)
        }

        val fcmTokens = fcmTokenRepository.findAll().toList()
        assertEquals(1, fcmTokens.size)

        with(fcmTokens[0]) {
            assertEquals(result.userId, fcmTokenId?.userId)
            assertEquals(fcmToken, fcmTokenId?.fcmToken)
        }
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

    @Test
    @Sql("user.sql")
    fun `Test getUserSettings`() {
        // given
        val token = "token"

        // when
        val settings = userService.getUserSettings(token)

        // then
        with(settings) {
            assertEquals("user name", userName)
            assertEquals(true, completed)
            assertEquals(false, skipped)
            assertEquals(true, joinedQueue)
            assertEquals(false, freeze)
            assertEquals(true, leftQueue)
            assertEquals(false, yourTurn)
        }
    }

    @Test
    @Sql("user.sql")
    fun `Test updateUserSettings empty userName exception`() {
        // given
        val token = "token"
        val userDTO = UpdateUserDto(
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
            userService.updateUserSettings(token, userDTO)
        }
    }

    @Test
    fun `Test updateUserSettings change userName and user repo save called`() {
        // given
        val token = "token"
        val userDTO = UpdateUserDto(
            userName = "new user",
            completed = null,
            skipped = null,
            joinedQueue = null,
            freeze = null,
            leftQueue = null,
            yourTurn = null
        )
        val userRepo = mockk<UserRepository>(relaxed = true)
        every { userRepo.findUserByToken(token) } returns User().apply {
            id = 1L
            name = "user name"
        }
        every { userRepo.save(any()) } returns User().apply {
            id = 1L
            name = "user name"
        }
        val service = UserService(userRepo, mockk())

        // when
        service.updateUserSettings(token, userDTO)

        // then
        verify(exactly = 1) { userRepo.save(any()) }
    }

    @Test
    fun `Test updateUserSettings change settings and user repo save called`() {
        // given
        val token = "token"
        val userDTO = UpdateUserDto(
            userName = null,
            completed = true,
            skipped = true,
            joinedQueue = true,
            freeze = true,
            leftQueue = true,
            yourTurn = true
        )
        val userRepo = mockk<UserRepository>(relaxed = true)
        every { userRepo.findUserByToken(token) } returns User().apply {
            id = 1L
            name = "user name"
        }
        every { userRepo.save(any()) } returns User().apply {
            id = 1L
            name = "user name"
        }
        val service = UserService(userRepo, mockk())

        // when
        service.updateUserSettings(token, userDTO)

        // then
        verify(exactly = 1) { userRepo.save(any()) }
    }

    @Test
    @Sql("user.sql")
    fun `Test updateUserSettings no updates and no changes in DB`() {
        // given
        val token = "token"
        val userDTO = UpdateUserDto(
            userName = null,
            completed = null,
            skipped = null,
            joinedQueue = null,
            freeze = null,
            leftQueue = null,
            yourTurn = null
        )

        // when
        val response = userService.updateUserSettings(token, userDTO)
        val userModel = userRepository.findAll().first()

        // then
        // response model
        with(response) {
            assertEquals("user name", userName)
            assertEquals(true, completed)
            assertEquals(false, skipped)
            assertEquals(true, joinedQueue)
            assertEquals(false, freeze)
            assertEquals(true, leftQueue)
            assertEquals(false, yourTurn)
        }

        // database
        with(userModel) {
            assertEquals("user name", name)
            assertEquals(true, completed)
            assertEquals(false, skipped)
            assertEquals(true, joinedQueue)
            assertEquals(false, freeze)
            assertEquals(true, leftQueue)
            assertEquals(false, yourTurn)
        }
    }

    @Test
    @Sql("user.sql")
    fun `Test updateUserSettings change userName`() {
        // given
        val token = "token"
        val expectedUserName = "new user name"
        val userDTO = UpdateUserDto(
            userName = "new user name",
            completed = null,
            skipped = null,
            joinedQueue = null,
            freeze = null,
            leftQueue = null,
            yourTurn = null
        )

        // when
        val response = userService.updateUserSettings(token, userDTO)
        val userModel = userRepository.findAll().first()

        // then
        // response model
        with(response) {
            assertEquals(expectedUserName, userName)
            assertEquals(true, completed)
            assertEquals(false, skipped)
            assertEquals(true, joinedQueue)
            assertEquals(false, freeze)
            assertEquals(true, leftQueue)
            assertEquals(false, yourTurn)
        }

        // database
        with(userModel) {
            assertEquals(expectedUserName, name)
            assertEquals(true, completed)
            assertEquals(false, skipped)
            assertEquals(true, joinedQueue)
            assertEquals(false, freeze)
            assertEquals(true, leftQueue)
            assertEquals(false, yourTurn)
        }
    }

    @Test
    @Sql("user.sql")
    fun `Test updateUserSettings change completed`() {
        // given
        val token = "token"
        val completed = false
        val skipped = false
        val joinedQueue = true
        val freeze = false
        val leftQueue = true
        val yourTurn = false
        val userDTO = UpdateUserDto(
            userName = null,
            completed = completed,
            skipped = null,
            joinedQueue = null,
            freeze = null,
            leftQueue = null,
            yourTurn = null
        )

        // when
        val response = userService.updateUserSettings(token, userDTO)
        val userModel = userRepository.findAll().first()

        // then
        // response model
        with(response) {
            assertEquals("user name", userName)
            assertEquals(completed, this.completed)
            assertEquals(skipped, this.skipped)
            assertEquals(joinedQueue, this.joinedQueue)
            assertEquals(freeze, this.freeze)
            assertEquals(leftQueue, this.leftQueue)
            assertEquals(yourTurn, this.yourTurn)
        }

        // database
        with(userModel) {
            assertEquals("user name", name)
            assertEquals(completed, this.completed)
            assertEquals(skipped, this.skipped)
            assertEquals(joinedQueue, this.joinedQueue)
            assertEquals(freeze, this.freeze)
            assertEquals(leftQueue, this.leftQueue)
            assertEquals(yourTurn, this.yourTurn)
        }
    }

    @Test
    @Sql("user.sql")
    fun `Test updateUserSettings change skipped`() {
        // given
        val token = "token"
        val completed = true
        val skipped = true
        val joinedQueue = true
        val freeze = false
        val leftQueue = true
        val yourTurn = false
        val userDTO = UpdateUserDto(
            userName = null,
            completed = null,
            skipped = skipped,
            joinedQueue = null,
            freeze = null,
            leftQueue = null,
            yourTurn = null
        )

        // when
        val response = userService.updateUserSettings(token, userDTO)
        val userModel = userRepository.findAll().first()

        // then
        // response model
        with(response) {
            assertEquals("user name", userName)
            assertEquals(completed, this.completed)
            assertEquals(skipped, this.skipped)
            assertEquals(joinedQueue, this.joinedQueue)
            assertEquals(freeze, this.freeze)
            assertEquals(leftQueue, this.leftQueue)
            assertEquals(yourTurn, this.yourTurn)
        }

        // database
        with(userModel) {
            assertEquals("user name", name)
            assertEquals(completed, this.completed)
            assertEquals(skipped, this.skipped)
            assertEquals(joinedQueue, this.joinedQueue)
            assertEquals(freeze, this.freeze)
            assertEquals(leftQueue, this.leftQueue)
            assertEquals(yourTurn, this.yourTurn)
        }
    }

    @Test
    @Sql("user.sql")
    fun `Test updateUserSettings change joinedQueue`() {
        // given
        val token = "token"
        val completed = true
        val skipped = false
        val joinedQueue = false
        val freeze = false
        val leftQueue = true
        val yourTurn = false
        val userDTO = UpdateUserDto(
            userName = null,
            completed = null,
            skipped = null,
            joinedQueue = joinedQueue,
            freeze = null,
            leftQueue = null,
            yourTurn = null
        )

        // when
        val response = userService.updateUserSettings(token, userDTO)
        val userModel = userRepository.findAll().first()

        // then
        // response model
        with(response) {
            assertEquals("user name", userName)
            assertEquals(completed, this.completed)
            assertEquals(skipped, this.skipped)
            assertEquals(joinedQueue, this.joinedQueue)
            assertEquals(freeze, this.freeze)
            assertEquals(leftQueue, this.leftQueue)
            assertEquals(yourTurn, this.yourTurn)
        }

        // database
        with(userModel) {
            assertEquals("user name", name)
            assertEquals(completed, this.completed)
            assertEquals(skipped, this.skipped)
            assertEquals(joinedQueue, this.joinedQueue)
            assertEquals(freeze, this.freeze)
            assertEquals(leftQueue, this.leftQueue)
            assertEquals(yourTurn, this.yourTurn)
        }
    }

    @Test
    @Sql("user.sql")
    fun `Test updateUserSettings change freeze`() {
        // given
        val token = "token"
        val completed = true
        val skipped = false
        val joinedQueue = true
        val freeze = true
        val leftQueue = true
        val yourTurn = false
        val userDTO = UpdateUserDto(
            userName = null,
            completed = null,
            skipped = null,
            joinedQueue = null,
            freeze = freeze,
            leftQueue = null,
            yourTurn = null
        )

        // when
        val response = userService.updateUserSettings(token, userDTO)
        val userModel = userRepository.findAll().first()

        // then
        // response model
        with(response) {
            assertEquals("user name", userName)
            assertEquals(completed, this.completed)
            assertEquals(skipped, this.skipped)
            assertEquals(joinedQueue, this.joinedQueue)
            assertEquals(freeze, this.freeze)
            assertEquals(leftQueue, this.leftQueue)
            assertEquals(yourTurn, this.yourTurn)
        }

        // database
        with(userModel) {
            assertEquals("user name", name)
            assertEquals(completed, this.completed)
            assertEquals(skipped, this.skipped)
            assertEquals(joinedQueue, this.joinedQueue)
            assertEquals(freeze, this.freeze)
            assertEquals(leftQueue, this.leftQueue)
            assertEquals(yourTurn, this.yourTurn)
        }
    }

    @Test
    @Sql("user.sql")
    fun `Test updateUserSettings change leftQueue`() {
        // given
        val token = "token"
        val completed = true
        val skipped = false
        val joinedQueue = true
        val freeze = false
        val leftQueue = false
        val yourTurn = false
        val userDTO = UpdateUserDto(
            userName = null,
            completed = null,
            skipped = null,
            joinedQueue = null,
            freeze = null,
            leftQueue = leftQueue,
            yourTurn = null
        )

        // when
        val response = userService.updateUserSettings(token, userDTO)
        val userModel = userRepository.findAll().first()

        // then
        // response model
        with(response) {
            assertEquals("user name", userName)
            assertEquals(completed, this.completed)
            assertEquals(skipped, this.skipped)
            assertEquals(joinedQueue, this.joinedQueue)
            assertEquals(freeze, this.freeze)
            assertEquals(leftQueue, this.leftQueue)
            assertEquals(yourTurn, this.yourTurn)
        }

        // database
        with(userModel) {
            assertEquals("user name", name)
            assertEquals(completed, this.completed)
            assertEquals(skipped, this.skipped)
            assertEquals(joinedQueue, this.joinedQueue)
            assertEquals(freeze, this.freeze)
            assertEquals(leftQueue, this.leftQueue)
            assertEquals(yourTurn, this.yourTurn)
        }
    }

    @Test
    @Sql("user.sql")
    fun `Test updateUserSettings change yourTurn`() {
        // given
        val token = "token"
        val completed = true
        val skipped = false
        val joinedQueue = true
        val freeze = false
        val leftQueue = true
        val yourTurn = true
        val userDTO = UpdateUserDto(
            userName = null,
            completed = null,
            skipped = null,
            joinedQueue = null,
            freeze = null,
            leftQueue = null,
            yourTurn = yourTurn
        )

        // when
        val response = userService.updateUserSettings(token, userDTO)
        val userModel = userRepository.findAll().first()

        // then
        // response model
        with(response) {
            assertEquals("user name", userName)
            assertEquals(completed, this.completed)
            assertEquals(skipped, this.skipped)
            assertEquals(joinedQueue, this.joinedQueue)
            assertEquals(freeze, this.freeze)
            assertEquals(leftQueue, this.leftQueue)
            assertEquals(yourTurn, this.yourTurn)
        }

        // database
        with(userModel) {
            assertEquals("user name", name)
            assertEquals(completed, this.completed)
            assertEquals(skipped, this.skipped)
            assertEquals(joinedQueue, this.joinedQueue)
            assertEquals(freeze, this.freeze)
            assertEquals(leftQueue, this.leftQueue)
            assertEquals(yourTurn, this.yourTurn)
        }
    }

    @Test
    @Sql("user.sql")
    fun `Test updateUserSettings update all settings`() {
        // given
        val token = "token"
        val completed = false
        val skipped = false
        val joinedQueue = false
        val freeze = false
        val leftQueue = false
        val yourTurn = false
        val userDTO = UpdateUserDto(
            userName = null,
            completed = completed,
            skipped = skipped,
            joinedQueue = joinedQueue,
            freeze = freeze,
            leftQueue = leftQueue,
            yourTurn = yourTurn
        )

        // when
        val response = userService.updateUserSettings(token, userDTO)
        val userModel = userRepository.findAll().first()

        // then
        // response model
        with(response) {
            assertEquals("user name", userName)
            assertEquals(completed, this.completed)
            assertEquals(skipped, this.skipped)
            assertEquals(joinedQueue, this.joinedQueue)
            assertEquals(freeze, this.freeze)
            assertEquals(leftQueue, this.leftQueue)
            assertEquals(yourTurn, this.yourTurn)
        }

        // database
        with(userModel) {
            assertEquals("user name", name)
            assertEquals(completed, this.completed)
            assertEquals(skipped, this.skipped)
            assertEquals(joinedQueue, this.joinedQueue)
            assertEquals(freeze, this.freeze)
            assertEquals(leftQueue, this.leftQueue)
            assertEquals(yourTurn, this.yourTurn)
        }
    }

    @Test
    @Sql("user.sql")
    fun `Test updateUserSettings update all settings and userName`() {
        // given
        val token = "token"
        val userName = "new user name"
        val completed = false
        val skipped = false
        val joinedQueue = false
        val freeze = false
        val leftQueue = false
        val yourTurn = false
        val userDTO = UpdateUserDto(
            userName = userName,
            completed = completed,
            skipped = skipped,
            joinedQueue = joinedQueue,
            freeze = freeze,
            leftQueue = leftQueue,
            yourTurn = yourTurn
        )

        // when
        val response = userService.updateUserSettings(token, userDTO)
        val userModel = userRepository.findAll().first()

        // then
        // response model
        with(response) {
            assertEquals(userName, userName)
            assertEquals(completed, this.completed)
            assertEquals(skipped, this.skipped)
            assertEquals(joinedQueue, this.joinedQueue)
            assertEquals(freeze, this.freeze)
            assertEquals(leftQueue, this.leftQueue)
            assertEquals(yourTurn, this.yourTurn)
        }

        // database
        with(userModel) {
            assertEquals(userName, name)
            assertEquals(completed, this.completed)
            assertEquals(skipped, this.skipped)
            assertEquals(joinedQueue, this.joinedQueue)
            assertEquals(freeze, this.freeze)
            assertEquals(leftQueue, this.leftQueue)
            assertEquals(yourTurn, this.yourTurn)
        }
    }
}
