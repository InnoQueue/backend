package com.innopolis.innoqueue.rest.v1

import com.innopolis.innoqueue.domain.user.dto.UpdateUserDto
import com.innopolis.innoqueue.domain.user.service.UserService
import com.innopolis.innoqueue.rest.v1.dto.NewUserDto
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test

class UserControllerTest {

    @Test
    fun `Test createNewUser service called`() {
        // given
        val userName = "user name"
        val fcmToken = "fcmToken"
        val service = mockk<UserService>(relaxed = true)
        val controller = UserController(service)

        // when
        controller.createNewUser(NewUserDto(userName, fcmToken))

        // then
        verify(exactly = 1) { service.createNewUser(userName, fcmToken) }
    }

    @Test
    fun `Test getUserSettings service called`() {
        // given
        val token = "token"
        val service = mockk<UserService>(relaxed = true)
        val controller = UserController(service)

        // when
        controller.getUserSettings(token)

        // then
        verify(exactly = 1) { service.getUserSettings(token) }
    }

    @Test
    fun `Test updateUserSettings service called`() {
        // given
        val token = "token"
        val userDTO = UpdateUserDto(
            userName = "userName",
            completed = true,
            skipped = false,
            joinedQueue = true,
            freeze = false,
            leftQueue = true,
            yourTurn = false
        )
        val service = mockk<UserService>(relaxed = true)
        val controller = UserController(service)

        // when
        controller.updateUserSettings(token, userDTO)

        // then
        verify(exactly = 1) { service.updateUserSettings(token, userDTO) }
    }
}
