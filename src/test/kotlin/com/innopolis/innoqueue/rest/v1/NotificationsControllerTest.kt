package com.innopolis.innoqueue.rest.v1

import com.innopolis.innoqueue.domain.notification.service.NotificationsListService
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.data.domain.Page

class NotificationsControllerTest {

    @Test
    fun `Test getNotifications service called`() {
        // given
        val token = "token"
        val service = mockk<NotificationsListService>(relaxed = true)
        every { service.getNotifications(token, any()) } returns Page.empty()
        val controller = NotificationsController(service)

        // when
        controller.getNotifications(token, 0, 50)

        // then
        verify(exactly = 1) { service.getNotifications(token, any()) }
    }

    @Test
    fun `Test getNotifications throws exception if page is less than 0`() {
        // given
        val token = "token"
        val service = mockk<NotificationsListService>(relaxed = true)
        every { service.getNotifications(token, any()) } returns Page.empty()
        val controller = NotificationsController(service)

        // when and then
        assertThrows<IllegalArgumentException> {
            controller.getNotifications(token, -1, 50)
        }
    }

    @Test
    fun `Test getNotifications throws exception if size is 0`() {
        // given
        val token = "token"
        val service = mockk<NotificationsListService>(relaxed = true)
        every { service.getNotifications(token, any()) } returns Page.empty()
        val controller = NotificationsController(service)

        // when and then
        assertThrows<IllegalArgumentException> {
            controller.getNotifications(token, 0, 0)
        }
    }

    @Test
    fun `Test getNotifications throws exception if size is negative`() {
        // given
        val token = "token"
        val service = mockk<NotificationsListService>(relaxed = true)
        every { service.getNotifications(token, any()) } returns Page.empty()
        val controller = NotificationsController(service)

        // when and then
        assertThrows<IllegalArgumentException> {
            controller.getNotifications(token, 0, -1)
        }
    }

    @Test
    fun `Test anyNewNotification service called`() {
        // given
        val token = "token"
        val service = mockk<NotificationsListService>(relaxed = true)
        val controller = NotificationsController(service)

        // when
        controller.anyNewNotification(token)

        // then
        verify(exactly = 1) { service.anyNewNotification(token) }
    }
}
