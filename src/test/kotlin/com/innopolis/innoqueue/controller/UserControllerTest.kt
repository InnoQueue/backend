package com.innopolis.innoqueue.controller

import com.innopolis.innoqueue.controller.dto.NewUserDTO
import com.innopolis.innoqueue.service.UserService
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test

class UserControllerTest {

    @Test
    fun `Test createNewUser service called`() {
        // given
        val userName = "testUser"
        val fcmToken = "123"
        val userService = mockk<UserService>(relaxed = true)
        val userController = UserController(userService)

        // when
        userController.createNewUser(NewUserDTO(userName, fcmToken))

        // then
        verify(exactly = 1) { userService.createNewUser(userName, fcmToken) }
    }
}
