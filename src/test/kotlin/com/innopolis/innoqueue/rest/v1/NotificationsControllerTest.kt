package com.innopolis.innoqueue.rest.v1

import com.innopolis.innoqueue.rest.v1.NotificationsController
import com.innopolis.innoqueue.services.NotificationsService
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test

class NotificationsControllerTest {

    @Test
    fun `Test getNotifications service called`() {
        // given
        val token = "token"
        val service = mockk<NotificationsService>(relaxed = true)
        val controller = NotificationsController(service)

        // when
        controller.getNotifications(token)

        // then
        verify(exactly = 1) { service.getNotifications(token) }
    }

    @Test
    fun `Test anyNewNotification service called`() {
        // given
        val token = "token"
        val service = mockk<NotificationsService>(relaxed = true)
        val controller = NotificationsController(service)

        // when
        controller.anyNewNotification(token)

        // then
        verify(exactly = 1) { service.anyNewNotification(token) }
    }

    @Test
    fun `Test clearOldNotifications service called`() {
        // given
        val service = mockk<NotificationsService>(relaxed = true)
        val controller = NotificationsController(service)

        // when
        controller.clearOldNotifications()

        // then
        verify(exactly = 1) { service.clearOldNotifications() }
    }
}
