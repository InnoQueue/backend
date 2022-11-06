package com.innopolis.innoqueue.services

import com.innopolis.innoqueue.dao.NotificationRepository
import com.innopolis.innoqueue.dao.QueueRepository
import com.innopolis.innoqueue.enums.NotificationsType
import com.innopolis.innoqueue.models.*
import com.innopolis.innoqueue.testcontainers.PostgresTestContainer
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.findByIdOrNull
import org.springframework.test.context.jdbc.Sql
import java.time.LocalDateTime

class NotificationsServiceTest : PostgresTestContainer() {

    @Autowired
    private lateinit var notificationsService: NotificationsService

    @Autowired
    private lateinit var notificationRepository: NotificationRepository

    @Test
    fun `Test getNotifications repos were called`() {
        // given
        val token = "11111"
        val firebaseMessagingService = mockk<FirebaseMessagingNotificationsService>(relaxed = true)
        val userService = mockk<UserService>(relaxed = true)
        every { userService.findUserNameById(any()) } returns null
        val queueRepository = mockk<QueueRepository>(relaxed = true)
        every { queueRepository.findByIdOrNull(any()) } returns null
        val notificationRepo = mockk<NotificationRepository>(relaxed = true)
        every { notificationRepo.findAllByToken(token) } returns listOf(Notification()
            .apply {
                isRead = true
                messageType = NotificationsType.YOUR_TURN
                participantId = 1
                queueId = 1
                date = LocalDateTime.now()
            })
        val service = NotificationsService(firebaseMessagingService, userService, queueRepository, notificationRepo)

        // when
        service.getNotifications(token)

        // then
        verify(atLeast = 1) { userService.findUserNameById(any()) }
        verify(atLeast = 1) { queueRepository.findByIdOrNull(any()) }
    }

    @Test
    @Sql("users.sql", "queues.sql", "notifications.sql")
    fun `Test getNotifications`() {
        // given
        val token = "11111"

        // when
        val notifications = notificationsService.getNotifications(token)

        // then
        assertEquals(3, notifications.unreadNotifications.size)
        assertEquals(3, notifications.allNotifications.size)

        assertEquals(NotificationsType.SHOOK, notifications.unreadNotifications[0].messageType)
        assertEquals(1, notifications.unreadNotifications[0].participantId)
        assertEquals("admin", notifications.unreadNotifications[0].participantName)
        assertEquals(39, notifications.unreadNotifications[0].queueId)
        assertEquals("Trash", notifications.unreadNotifications[0].queueName)

        assertEquals(NotificationsType.YOUR_TURN, notifications.unreadNotifications[1].messageType)
        assertEquals(1, notifications.unreadNotifications[1].participantId)
        assertEquals("admin", notifications.unreadNotifications[1].participantName)
        assertEquals(39, notifications.unreadNotifications[1].queueId)
        assertEquals("Trash", notifications.unreadNotifications[1].queueName)

        assertEquals(NotificationsType.COMPLETED, notifications.unreadNotifications[2].messageType)
        assertEquals(5, notifications.unreadNotifications[2].participantId)
        assertEquals("Ivan", notifications.unreadNotifications[2].participantName)
        assertEquals(39, notifications.unreadNotifications[2].queueId)
        assertEquals("Trash", notifications.unreadNotifications[2].queueName)

        assertEquals(NotificationsType.COMPLETED, notifications.allNotifications[0].messageType)
        assertEquals(2, notifications.allNotifications[0].participantId)
        assertEquals("Emil", notifications.allNotifications[0].participantName)
        assertEquals(44, notifications.allNotifications[0].queueId)
        assertEquals("Bring Water", notifications.allNotifications[0].queueName)

        assertEquals(NotificationsType.SKIPPED, notifications.allNotifications[1].messageType)
        assertEquals(3, notifications.allNotifications[1].participantId)
        assertEquals("Roman", notifications.allNotifications[1].participantName)
        assertEquals(400, notifications.allNotifications[1].queueId)
        assertEquals("Deleted queue", notifications.allNotifications[1].queueName)

        assertEquals(NotificationsType.JOINED_QUEUE, notifications.allNotifications[2].messageType)
        assertEquals(5, notifications.allNotifications[2].participantId)
        assertEquals("Ivan", notifications.allNotifications[2].participantName)
        assertEquals(44, notifications.allNotifications[2].queueId)
        assertEquals("Bring Water", notifications.allNotifications[2].queueName)
    }

    @Test
    @Sql("users.sql", "queues.sql", "notifications.sql")
    fun `Test getNotifications all read`() {
        // given
        val token = "11111"

        // when
        notificationsService.getNotifications(token)
        val notifications = notificationsService.getNotifications(token)

        // then
        assertEquals(0, notifications.unreadNotifications.size)
        assertEquals(6, notifications.allNotifications.size)
    }

    @Test
    fun `Test anyNewNotification notification repo called`() {
        // given
        val token = "token"
        val firebaseMessagingService = mockk<FirebaseMessagingNotificationsService>(relaxed = true)
        val userService = mockk<UserService>(relaxed = true)
        val queueRepository = mockk<QueueRepository>(relaxed = true)
        val notificationRepo = mockk<NotificationRepository>(relaxed = true)
        every { notificationRepo.anyUnreadNotification(token) } returns true
        val service = NotificationsService(firebaseMessagingService, userService, queueRepository, notificationRepo)

        // when
        service.anyNewNotification(token)

        // then
        verify(exactly = 1) { notificationRepo.anyUnreadNotification(token) }
    }

    @Test
    @Sql("users.sql", "queues.sql", "notifications.sql")
    fun `Test anyNewNotification unread`() {
        // given
        val token = "11111"

        // when
        val result = notificationsService.anyNewNotification(token)

        // then
        assertEquals(true, result.anyNew)
    }

    @Test
    @Sql("users.sql", "queues.sql", "notifications.sql")
    fun `Test anyNewNotification all read`() {
        // given
        val token = "11111"

        // when
        notificationsService.getNotifications(token)
        val result = notificationsService.anyNewNotification(token)

        // then
        assertEquals(false, result.anyNew)
    }

    @Test
    fun `Test clearOldNotifications notification repo called`() {
        // given
        val firebaseMessagingService = mockk<FirebaseMessagingNotificationsService>(relaxed = true)
        val userService = mockk<UserService>(relaxed = true)
        val queueRepository = mockk<QueueRepository>(relaxed = true)
        val notificationRepo = mockk<NotificationRepository>(relaxed = true)
        val service = NotificationsService(firebaseMessagingService, userService, queueRepository, notificationRepo)

        // when
        service.clearOldNotifications()

        // then
        verify(exactly = 1) { notificationRepo.findAllExpiredNotifications() }
        verify(exactly = 1) { notificationRepo.deleteAll(any()) }
    }

    @Test
    @Sql("users.sql", "queues.sql", "notifications.sql")
    fun `Test clearOldNotifications`() {
        // when
        val resultDto = notificationsService.clearOldNotifications()

        // then
        val notifications = notificationRepository.findAll().toList()
        assertEquals("Old notifications were deleted", resultDto.result)
        assertEquals(21, notifications.size)
        assertTrue(notifications.none { it.date!! <= LocalDateTime.now().minusWeeks(2) })
    }

    @Test
    @Sql("users.sql", "user_settings.sql", "queues.sql", "user_queue.sql")
    fun `Test sendNotificationMessage YOUR_TURN`() {
        // given
        val notificationType = NotificationsType.YOUR_TURN
        val participantModel = User().apply {
            id = 1L
            name = "admin"
            settings = getUserSettings()
        }
        val queueModel = getQueueModel(participantModel)

        // when
        notificationsService.sendNotificationMessage(notificationType, participantModel, queueModel)

        // then
        val notifications = notificationRepository.findAll().toList()
        assertEquals(4, notifications.size)
        assertTrue(notifications.all { it.messageType == NotificationsType.YOUR_TURN })
        assertTrue(notifications.all { it.participantId == 1L })
        assertTrue(notifications.none { it.user?.id == 4L })
    }

    @Test
    @Sql("users.sql", "user_settings.sql", "queues.sql", "user_queue.sql")
    fun `Test sendNotificationMessage COMPLETED`() {
        // given
        val notificationType = NotificationsType.COMPLETED
        val participantModel = User().apply {
            id = 1L
            name = "admin"
            settings = getUserSettings()
        }
        val queueModel = getQueueModel(participantModel)

        // when
        notificationsService.sendNotificationMessage(notificationType, participantModel, queueModel)

        // then
        val notifications = notificationRepository.findAll().toList()
        assertEquals(4, notifications.size)
        assertTrue(notifications.all { it.messageType == NotificationsType.COMPLETED })
        assertTrue(notifications.all { it.participantId == 1L })
        assertTrue(notifications.none { it.user?.id == 4L })
    }

    @Test
    @Sql("users.sql", "user_settings.sql", "queues.sql", "user_queue.sql")
    fun `Test sendNotificationMessage SKIPPED`() {
        // given
        val notificationType = NotificationsType.SKIPPED
        val participantModel = User().apply {
            id = 1L
            name = "admin"
            settings = getUserSettings()
        }
        val queueModel = getQueueModel(participantModel)

        // when
        notificationsService.sendNotificationMessage(notificationType, participantModel, queueModel)

        // then
        val notifications = notificationRepository.findAll().toList()
        assertEquals(4, notifications.size)
        assertTrue(notifications.all { it.messageType == NotificationsType.SKIPPED })
        assertTrue(notifications.all { it.participantId == 1L })
        assertTrue(notifications.none { it.user?.id == 4L })
    }

    @Test
    @Sql("users.sql", "user_settings.sql", "queues.sql", "user_queue.sql")
    fun `Test sendNotificationMessage SHOOK`() {
        // given
        val notificationType = NotificationsType.SHOOK
        val participantModel = User().apply {
            id = 1L
            name = "admin"
            settings = getUserSettings()
        }
        val queueModel = getQueueModel(participantModel)

        // when
        notificationsService.sendNotificationMessage(notificationType, participantModel, queueModel)

        // then
        val notifications = notificationRepository.findAll().toList()
        assertEquals(1, notifications.size)
        assertTrue(notifications.all { it.messageType == NotificationsType.SHOOK })
        assertTrue(notifications.all { it.participantId == 1L })
    }

    @Test
    @Sql("users.sql", "user_settings.sql", "queues.sql", "user_queue.sql")
    fun `Test sendNotificationMessage FROZEN`() {
        // given
        val notificationType = NotificationsType.FROZEN
        val participantModel = User().apply {
            id = 1L
            name = "admin"
            settings = getUserSettings()
        }
        val queueModel = getQueueModel(participantModel)

        // when
        notificationsService.sendNotificationMessage(notificationType, participantModel, queueModel)

        // then
        val notifications = notificationRepository.findAll().toList()
        assertEquals(4, notifications.size)
        assertTrue(notifications.all { it.messageType == NotificationsType.FROZEN })
        assertTrue(notifications.all { it.participantId == 1L })
        assertTrue(notifications.none { it.user?.id == 4L })
    }

    @Test
    @Sql("users.sql", "user_settings.sql", "queues.sql", "user_queue.sql")
    fun `Test sendNotificationMessage UNFROZEN`() {
        // given
        val notificationType = NotificationsType.UNFROZEN
        val participantModel = User().apply {
            id = 1L
            name = "admin"
            settings = getUserSettings()
        }
        val queueModel = getQueueModel(participantModel)

        // when
        notificationsService.sendNotificationMessage(notificationType, participantModel, queueModel)

        // then
        val notifications = notificationRepository.findAll().toList()
        assertEquals(4, notifications.size)
        assertTrue(notifications.all { it.messageType == NotificationsType.UNFROZEN })
        assertTrue(notifications.all { it.participantId == 1L })
        assertTrue(notifications.none { it.user?.id == 4L })
    }

    @Test
    @Sql("users.sql", "user_settings.sql", "queues.sql", "user_queue.sql")
    fun `Test sendNotificationMessage JOINED_QUEUE`() {
        // given
        val notificationType = NotificationsType.JOINED_QUEUE
        val participantModel = User().apply {
            id = 1L
            name = "admin"
            settings = getUserSettings()
        }
        val queueModel = getQueueModel(participantModel)

        // when
        notificationsService.sendNotificationMessage(notificationType, participantModel, queueModel)

        // then
        val notifications = notificationRepository.findAll().toList()
        assertEquals(4, notifications.size)
        assertTrue(notifications.all { it.messageType == NotificationsType.JOINED_QUEUE })
        assertTrue(notifications.all { it.participantId == 1L })
        assertTrue(notifications.none { it.user?.id == 4L })
    }

    @Test
    @Sql("users.sql", "user_settings.sql", "queues.sql", "user_queue.sql")
    fun `Test sendNotificationMessage LEFT_QUEUE`() {
        // given
        val notificationType = NotificationsType.LEFT_QUEUE
        val participantModel = User().apply {
            id = 1L
            name = "admin"
            settings = getUserSettings()
        }
        val queueModel = getQueueModel(participantModel)

        // when
        notificationsService.sendNotificationMessage(notificationType, participantModel, queueModel)

        // then
        val notifications = notificationRepository.findAll().toList()
        assertEquals(4, notifications.size)
        assertTrue(notifications.all { it.messageType == NotificationsType.LEFT_QUEUE })
        assertTrue(notifications.all { it.participantId == 1L })
        assertTrue(notifications.none { it.user?.id == 4L })
    }

    @Test
    @Sql("users.sql", "user_settings.sql", "queues.sql", "user_queue.sql")
    fun `Test sendNotificationMessage DELETE_QUEUE`() {
        // given
        val notificationType = NotificationsType.DELETE_QUEUE
        val participantModel = User().apply {
            id = 1L
            name = "admin"
            settings = getUserSettings()
        }
        val queueModel = getQueueModel(participantModel)

        // when
        notificationsService.sendNotificationMessage(notificationType, participantModel, queueModel)

        // then
        val notifications = notificationRepository.findAll().toList()
        assertEquals(5, notifications.size)
        assertTrue(notifications.all { it.messageType == NotificationsType.DELETE_QUEUE })
        assertTrue(notifications.all { it.participantId == 1L })
    }

    private fun getQueueModel(participantModel: User): Queue {
        val queueModel = Queue().apply {
            id = 44L
            name = "Bring Water"
            color = "BLUE"
            creator = participantModel
            trackExpenses = false
            currentUser = participantModel
        }
        queueModel.userQueues = mutableSetOf(
            UserQueue().apply {
                id = 1L
                queue = queueModel
                user = participantModel
                isActive = true
                skips = 0
                expenses = 0.0
                isImportant = false
                dateJoined = LocalDateTime.of(2022, 11, 4, 12, 12, 12)
            },
            getUserQueue(2L, 2L, "Emil", queueModel),
            getUserQueue(3L, 3L, "Roman", queueModel),
            getUserQueue(4L, 4L, "Timur", queueModel, false),
            getUserQueue(5L, 5L, "Ivan", queueModel)
        )
        return queueModel
    }

    private fun getUserQueue(
        userQueueId: Long,
        userId: Long,
        userName: String,
        queueModel: Queue,
        enableUserSettings: Boolean = true
    ) =
        UserQueue().apply {
            id = userQueueId
            queue = queueModel
            user = User().apply {
                id = userId
                name = userName
                settings = getUserSettings(enableUserSettings)
            }
            isActive = true
            skips = 0
            expenses = 0.0
            isImportant = false
            dateJoined = LocalDateTime.of(2022, 11, 4, 12, 12, 12)
        }

    private fun getUserSettings(value: Boolean = true) = UserSettings().apply {
        completed = value
        skipped = value
        joinedQueue = value
        freeze = value
        leftQueue = value
        yourTurn = value
    }
}
