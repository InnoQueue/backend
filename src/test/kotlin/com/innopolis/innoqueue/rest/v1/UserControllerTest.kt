package com.innopolis.innoqueue.rest.v1

import com.innopolis.innoqueue.rest.v1.UserController
import com.innopolis.innoqueue.rest.v1.dto.NewUserDTO
import com.innopolis.innoqueue.services.UserService
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
        controller.createNewUser(NewUserDTO(userName, fcmToken))

        // then
        verify(exactly = 1) { service.createNewUser(userName, fcmToken) }
    }
}
