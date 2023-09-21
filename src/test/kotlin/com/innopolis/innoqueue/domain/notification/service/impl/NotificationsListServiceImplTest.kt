package com.innopolis.innoqueue.domain.notification.service.impl

import com.innopolis.innoqueue.domain.notification.dao.NotificationRepository
import com.innopolis.innoqueue.domain.notification.enums.NotificationType
import com.innopolis.innoqueue.domain.notification.model.Notification
import com.innopolis.innoqueue.domain.queue.dao.QueueRepository
import com.innopolis.innoqueue.domain.user.dao.UserRepository
import com.innopolis.innoqueue.domain.user.service.UserService
import com.innopolis.innoqueue.testcontainer.PostgresTestContainer
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.test.context.jdbc.Sql
import java.time.LocalDateTime

class NotificationsListServiceImplTest : PostgresTestContainer() {

    @Autowired
    private lateinit var notificationsListServiceImpl: NotificationsListServiceImpl

    @Autowired
    private lateinit var notificationRepository: NotificationRepository

    @Autowired
    private lateinit var queueRepository: QueueRepository

    @Autowired
    private lateinit var userRepository: UserRepository

    val pageable: Pageable = PageRequest.of(
        0,
        100,
        Sort.by("is_read").and(Sort.by("date").descending())
    )

    @Test
    fun `Test getNotifications repos were called`() {
        // given
        val token = "11111"
        val notificationRepo = mockk<NotificationRepository>(relaxed = true)
        every { notificationRepo.findAllByToken(token) } returns listOf(
            Notification()
                .apply {
                    id = 1L
                    isRead = true
                    messageType = NotificationType.YOUR_TURN
                    participantId = 1
                    queueId = 1
                    date = LocalDateTime.now()
                })
        val service = NotificationsListServiceImpl(
            mockk(),
            mockk(),
            notificationRepo
        )

        // when
        service.getNotifications(token, pageable)

        // then
        verify(exactly = 1) { notificationRepo.findAllByToken(token, pageable) }
    }

    @Test
    @Sql(
        "/com/innopolis/innoqueue/domain/queue/service/impl/users.sql",
        "fcm_token.sql",
        "/com/innopolis/innoqueue/domain/queue/service/impl/queues.sql",
        "notification.sql"
    )
    fun `Test getNotifications`() {
        // given
        val token = "11111"

        // when
        queueRepository.deleteById(40L)
        userRepository.deleteById(3L)
        val notifications = notificationsListServiceImpl.getNotifications(token, pageable).content

        // then
        assertEquals(6, notifications.size)

        with(notifications[0]) {
            assertEquals(NotificationType.SHOOK, messageType)
            assertEquals(1, participantId)
            assertEquals("admin", participantName)
            assertEquals(39, queueId)
            assertEquals("Trash", queueName)
            assertEquals(false, read)
        }

        with(notifications[1]) {
            assertEquals(NotificationType.YOUR_TURN, messageType)
            assertEquals(1, participantId)
            assertEquals("admin", participantName)
            assertEquals(39, queueId)
            assertEquals("Trash", queueName)
            assertEquals(false, read)
        }

        with(notifications[2]) {
            assertEquals(NotificationType.COMPLETED, messageType)
            assertEquals(5, participantId)
            assertEquals("Ivan", participantName)
            assertEquals(39, queueId)
            assertEquals("Trash", queueName)
            assertEquals(false, read)
        }

        with(notifications[3]) {
            assertEquals(NotificationType.COMPLETED, messageType)
            assertEquals(2, participantId)
            assertEquals("Emil", participantName)
            assertEquals(44, queueId)
            assertEquals("Bring Water", queueName)
            assertEquals(true, read)
        }

        with(notifications[4]) {
            assertEquals(NotificationType.SKIPPED, messageType)
            assertEquals(null, participantId)
            assertEquals("Deleted user", participantName)
            assertEquals(null, queueId)
            assertEquals("Deleted queue", queueName)
            assertEquals(true, read)
        }

        with(notifications[5]) {
            assertEquals(NotificationType.JOINED_QUEUE, messageType)
            assertEquals(5, participantId)
            assertEquals("Ivan", participantName)
            assertEquals(44, queueId)
            assertEquals("Bring Water", queueName)
            assertEquals(true, read)
        }
    }

    @Test
    @Sql(
        "/com/innopolis/innoqueue/domain/queue/service/impl/users.sql",
        "fcm_token.sql",
        "/com/innopolis/innoqueue/domain/queue/service/impl/queues.sql",
        "notification.sql"
    )
    fun `Test readNotifications read specified ids`() {
        // given
        val token = "11111"
        val notificationIds = listOf(26L, 21L)

        // when
        notificationsListServiceImpl.readNotifications(token, notificationIds)
        val notifications = notificationsListServiceImpl.getNotifications(token, pageable).content

        // then
        assertTrue(notifications.none { it.notificationId in notificationIds && !it.read })
    }

    @Test
    @Sql(
        "/com/innopolis/innoqueue/domain/queue/service/impl/users.sql",
        "fcm_token.sql",
        "/com/innopolis/innoqueue/domain/queue/service/impl/queues.sql",
        "notification.sql"
    )
    fun `Test readNotifications all read`() {
        // given
        val token = "11111"

        // when
        notificationsListServiceImpl.readNotifications(token, null)
        val notifications = notificationsListServiceImpl.getNotifications(token, pageable).content

        // then
        assertTrue(notifications.none { !it.read })
    }

    @Test
    fun `Test anyNewNotification notification repo called`() {
        // given
        val token = "token"
        val userService = mockk<UserService>(relaxed = true)
        val queueRepository = mockk<QueueRepository>(relaxed = true)
        val notificationRepo = mockk<NotificationRepository>(relaxed = true)
        every { notificationRepo.anyUnreadNotification(token) } returns true
        val service = NotificationsListServiceImpl(
            userService,
            queueRepository,
            notificationRepo
        )

        // when
        service.anyNewNotification(token)

        // then
        verify(exactly = 1) { notificationRepo.anyUnreadNotification(token) }
    }

    @Test
    @Sql(
        "/com/innopolis/innoqueue/domain/queue/service/impl/users.sql",
        "fcm_token.sql",
        "/com/innopolis/innoqueue/domain/queue/service/impl/queues.sql",
        "notification.sql"
    )
    fun `Test anyNewNotification unread`() {
        // given
        val token = "11111"

        // when
        val result = notificationsListServiceImpl.anyNewNotification(token)

        // then
        assertEquals(true, result.anyNew)
    }

    @Test
    @Sql(
        "/com/innopolis/innoqueue/domain/queue/service/impl/users.sql",
        "fcm_token.sql",
        "/com/innopolis/innoqueue/domain/queue/service/impl/queues.sql",
        "notification.sql"
    )
    fun `Test anyNewNotification read all`() {
        // given
        val token = "11111"

        // when
        notificationsListServiceImpl.readNotifications(token)
        val result = notificationsListServiceImpl.anyNewNotification(token)

        // then
        assertEquals(false, result.anyNew)
    }

    @Test
    fun `Test clearOldNotifications notification repo called`() {
        // given
        val userService = mockk<UserService>(relaxed = true)
        val queueRepository = mockk<QueueRepository>(relaxed = true)
        val notificationRepo = mockk<NotificationRepository>(relaxed = true)
        val service = NotificationsListServiceImpl(
            userService,
            queueRepository,
            notificationRepo
        )

        // when
        service.clearOldNotifications()

        // then
        verify(exactly = 1) { notificationRepo.findAllExpiredNotifications() }
        verify(exactly = 1) { notificationRepo.deleteAll(any()) }
    }

    @Test
    @Sql(
        "/com/innopolis/innoqueue/domain/queue/service/impl/users.sql",
        "fcm_token.sql",
        "/com/innopolis/innoqueue/domain/queue/service/impl/queues.sql",
        "notification.sql"
    )
    fun `Test clearOldNotifications`() {
        // when
        val resultDto = notificationsListServiceImpl.clearOldNotifications()

        // then
        val notifications = notificationRepository.findAll().toList()
        assertEquals("Old notifications were deleted", resultDto.result)
        assertEquals(21, notifications.size)
        assertTrue(notifications.none { it.date!! <= LocalDateTime.now().minusWeeks(2) })
    }

    @Test
    @Sql(
        "/com/innopolis/innoqueue/domain/queue/service/impl/users.sql",
        "fcm_token.sql",
        "/com/innopolis/innoqueue/domain/queue/service/impl/queues.sql",
        "notification.sql"
    )
    fun `Test deleteNotifications delete specified ids`() {
        // given
        val token = "11111"
        val deletedNotificationIds = listOf(26L, 21L)

        // when
        notificationsListServiceImpl.deleteNotifications(token, listOf(26L, 21L))
        val notifications = notificationsListServiceImpl.getNotifications(token, pageable).content

        // then
        assertTrue(notifications.none { it.notificationId in deletedNotificationIds })
    }

    @Test
    @Sql(
        "/com/innopolis/innoqueue/domain/queue/service/impl/users.sql",
        "fcm_token.sql",
        "/com/innopolis/innoqueue/domain/queue/service/impl/queues.sql",
        "notification.sql"
    )
    fun `Test deleteNotifications delete all`() {
        // given
        val token = "11111"

        // when
        notificationsListServiceImpl.deleteNotifications(token)
        val notifications = notificationsListServiceImpl.getNotifications(token, pageable).content

        // then
        assertEquals(0, notifications.size)
    }

    @Test
    @Sql(
        "/com/innopolis/innoqueue/domain/queue/service/impl/users.sql",
        "fcm_token.sql",
        "/com/innopolis/innoqueue/domain/queue/service/impl/queues.sql",
        "notification.sql"
    )
    fun `Test deleteNotificationById`() {
        // given
        val token = "11111"
        val deletedNotificationId = 26L

        // when
        notificationsListServiceImpl.deleteNotificationById(token, deletedNotificationId)
        val notifications = notificationsListServiceImpl.getNotifications(token, pageable).content

        // then
        assertTrue(notifications.none { it.notificationId == deletedNotificationId })
    }
}
