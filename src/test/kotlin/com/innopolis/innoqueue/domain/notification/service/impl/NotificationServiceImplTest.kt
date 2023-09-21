package com.innopolis.innoqueue.domain.notification.service.impl

import com.innopolis.innoqueue.domain.fcmtoken.service.FcmTokenService
import com.innopolis.innoqueue.webclients.firebase.service.FirebaseMessagingNotificationsService
import com.innopolis.innoqueue.domain.notification.dao.NotificationRepository
import com.innopolis.innoqueue.domain.notification.enums.NotificationType
import com.innopolis.innoqueue.domain.notification.model.Notification
import com.innopolis.innoqueue.domain.queue.dao.QueueRepository
import com.innopolis.innoqueue.domain.queue.model.Queue
import com.innopolis.innoqueue.domain.user.dao.UserRepository
import com.innopolis.innoqueue.domain.user.model.User
import com.innopolis.innoqueue.domain.user.service.UserService
import com.innopolis.innoqueue.domain.userqueue.dao.UserQueueRepository
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

class NotificationServiceImplTest : PostgresTestContainer() {

    @Autowired
    private lateinit var notificationService: NotificationServiceImpl

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
        val service = NotificationServiceImpl(
            mockk(),
            mockk(),
            mockk(),
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
        val notifications = notificationService.getNotifications(token, pageable).content

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
        notificationService.readNotifications(token, notificationIds)
        val notifications = notificationService.getNotifications(token, pageable).content

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
        notificationService.readNotifications(token, null)
        val notifications = notificationService.getNotifications(token, pageable).content

        // then
        assertTrue(notifications.none { !it.read })
    }

    @Test
    fun `Test anyNewNotification notification repo called`() {
        // given
        val token = "token"
        val firebaseMessagingService = mockk<FirebaseMessagingNotificationsService>(relaxed = true)
        val userService = mockk<UserService>(relaxed = true)
        val fcmService = mockk<FcmTokenService>(relaxed = true)
        val queueRepository = mockk<QueueRepository>(relaxed = true)
        val userQueueRepository = mockk<UserQueueRepository>(relaxed = true)
        val notificationRepo = mockk<NotificationRepository>(relaxed = true)
        every { notificationRepo.anyUnreadNotification(token) } returns true
        val service = NotificationServiceImpl(
            firebaseMessagingService,
            userService,
            fcmService,
            queueRepository,
            userQueueRepository,
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
        val result = notificationService.anyNewNotification(token)

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
        notificationService.readNotifications(token)
        val result = notificationService.anyNewNotification(token)

        // then
        assertEquals(false, result.anyNew)
    }

    @Test
    fun `Test clearOldNotifications notification repo called`() {
        // given
        val firebaseMessagingService = mockk<FirebaseMessagingNotificationsService>(relaxed = true)
        val userService = mockk<UserService>(relaxed = true)
        val fcmService = mockk<FcmTokenService>(relaxed = true)
        val queueRepository = mockk<QueueRepository>(relaxed = true)
        val userQueueRepository = mockk<UserQueueRepository>(relaxed = true)
        val notificationRepo = mockk<NotificationRepository>(relaxed = true)
        val service = NotificationServiceImpl(
            firebaseMessagingService,
            userService,
            fcmService,
            queueRepository,
            userQueueRepository,
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
        val resultDto = notificationService.clearOldNotifications()

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
        notificationService.deleteNotifications(token, listOf(26L, 21L))
        val notifications = notificationService.getNotifications(token, pageable).content

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
        notificationService.deleteNotifications(token)
        val notifications = notificationService.getNotifications(token, pageable).content

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
        notificationService.deleteNotificationById(token, deletedNotificationId)
        val notifications = notificationService.getNotifications(token, pageable).content

        // then
        assertTrue(notifications.none { it.notificationId == deletedNotificationId })
    }

    @Test
    @Sql(
        "/com/innopolis/innoqueue/domain/queue/service/impl/users.sql",
        "fcm_token.sql",
        "/com/innopolis/innoqueue/domain/queue/service/impl/queues.sql",
        "/com/innopolis/innoqueue/domain/queue/service/impl/user_queue.sql"
    )
    fun `Test sendNotificationMessage YOUR_TURN`() {
        // given
        val notificationType = NotificationType.YOUR_TURN
        val participantModel = getUser()
        val queueModel = getQueueModel(participantModel)

        // when
        notificationService.sendNotificationMessage(
            notificationType,
            participantModel.id!!,
            participantModel.name!!,
            queueModel.queueId!!,
            queueModel.name!!
        )

        // then
        val notifications = notificationRepository.findAll().toList()
        assertEquals(4, notifications.size)
        assertTrue(notifications.all { it.messageType == NotificationType.YOUR_TURN })
        assertTrue(notifications.all { it.participantId == 1L })
        assertTrue(notifications.none { it.user?.id == 4L })
    }

    @Test
    @Sql(
        "/com/innopolis/innoqueue/domain/queue/service/impl/users.sql",
        "fcm_token.sql",
        "/com/innopolis/innoqueue/domain/queue/service/impl/queues.sql",
        "/com/innopolis/innoqueue/domain/queue/service/impl/user_queue.sql"
    )
    fun `Test sendNotificationMessage COMPLETED`() {
        // given
        val notificationType = NotificationType.COMPLETED
        val participantModel = getUser()
        val queueModel = getQueueModel(participantModel)

        // when
        notificationService.sendNotificationMessage(
            notificationType,
            participantModel.id!!,
            participantModel.name!!,
            queueModel.queueId!!,
            queueModel.name!!
        )

        // then
        val notifications = notificationRepository.findAll().toList()
        assertEquals(4, notifications.size)
        assertTrue(notifications.all { it.messageType == NotificationType.COMPLETED })
        assertTrue(notifications.all { it.participantId == 1L })
        assertTrue(notifications.none { it.user?.id == 4L })
    }

    @Test
    @Sql(
        "/com/innopolis/innoqueue/domain/queue/service/impl/users.sql",
        "fcm_token.sql",
        "/com/innopolis/innoqueue/domain/queue/service/impl/queues.sql",
        "/com/innopolis/innoqueue/domain/queue/service/impl/user_queue.sql"
    )
    fun `Test sendNotificationMessage SKIPPED`() {
        // given
        val notificationType = NotificationType.SKIPPED
        val participantModel = getUser()
        val queueModel = getQueueModel(participantModel)

        // when
        notificationService.sendNotificationMessage(
            notificationType,
            participantModel.id!!,
            participantModel.name!!,
            queueModel.queueId!!,
            queueModel.name!!
        )

        // then
        val notifications = notificationRepository.findAll().toList()
        assertEquals(4, notifications.size)
        assertTrue(notifications.all { it.messageType == NotificationType.SKIPPED })
        assertTrue(notifications.all { it.participantId == 1L })
        assertTrue(notifications.none { it.user?.id == 4L })
    }

    @Test
    @Sql(
        "/com/innopolis/innoqueue/domain/queue/service/impl/users.sql",
        "fcm_token.sql",
        "/com/innopolis/innoqueue/domain/queue/service/impl/queues.sql",
        "/com/innopolis/innoqueue/domain/queue/service/impl/user_queue.sql"
    )
    fun `Test sendNotificationMessage SHOOK`() {
        // given
        val notificationType = NotificationType.SHOOK
        val participantModel = getUser()
        val queueModel = getQueueModel(participantModel)

        // when
        notificationService.sendNotificationMessage(
            notificationType,
            participantModel.id!!,
            participantModel.name!!,
            queueModel.queueId!!,
            queueModel.name!!
        )

        // then
        val notifications = notificationRepository.findAll().toList()
        assertEquals(1, notifications.size)
        assertTrue(notifications.all { it.messageType == NotificationType.SHOOK })
        assertTrue(notifications.all { it.participantId == 1L })
    }

    @Test
    @Sql(
        "/com/innopolis/innoqueue/domain/queue/service/impl/users.sql",
        "fcm_token.sql",
        "/com/innopolis/innoqueue/domain/queue/service/impl/queues.sql",
        "/com/innopolis/innoqueue/domain/queue/service/impl/user_queue.sql"
    )
    fun `Test sendNotificationMessage FROZEN`() {
        // given
        val notificationType = NotificationType.FROZEN
        val participantModel = getUser()
        val queueModel = getQueueModel(participantModel)

        // when
        notificationService.sendNotificationMessage(
            notificationType,
            participantModel.id!!,
            participantModel.name!!,
            queueModel.queueId!!,
            queueModel.name!!
        )

        // then
        val notifications = notificationRepository.findAll().toList()
        assertEquals(4, notifications.size)
        assertTrue(notifications.all { it.messageType == NotificationType.FROZEN })
        assertTrue(notifications.all { it.participantId == 1L })
        assertTrue(notifications.none { it.user?.id == 4L })
    }

    @Test
    @Sql(
        "/com/innopolis/innoqueue/domain/queue/service/impl/users.sql",
        "fcm_token.sql",
        "/com/innopolis/innoqueue/domain/queue/service/impl/queues.sql",
        "/com/innopolis/innoqueue/domain/queue/service/impl/user_queue.sql"
    )
    fun `Test sendNotificationMessage UNFROZEN`() {
        // given
        val notificationType = NotificationType.UNFROZEN
        val participantModel = getUser()
        val queueModel = getQueueModel(participantModel)

        // when
        notificationService.sendNotificationMessage(
            notificationType,
            participantModel.id!!,
            participantModel.name!!,
            queueModel.queueId!!,
            queueModel.name!!
        )

        // then
        val notifications = notificationRepository.findAll().toList()
        assertEquals(4, notifications.size)
        assertTrue(notifications.all { it.messageType == NotificationType.UNFROZEN })
        assertTrue(notifications.all { it.participantId == 1L })
        assertTrue(notifications.none { it.user?.id == 4L })
    }

    @Test
    @Sql(
        "/com/innopolis/innoqueue/domain/queue/service/impl/users.sql",
        "fcm_token.sql",
        "/com/innopolis/innoqueue/domain/queue/service/impl/queues.sql",
        "/com/innopolis/innoqueue/domain/queue/service/impl/user_queue.sql"
    )
    fun `Test sendNotificationMessage JOINED_QUEUE`() {
        // given
        val notificationType = NotificationType.JOINED_QUEUE
        val participantModel = getUser()
        val queueModel = getQueueModel(participantModel)

        // when
        notificationService.sendNotificationMessage(
            notificationType,
            participantModel.id!!,
            participantModel.name!!,
            queueModel.queueId!!,
            queueModel.name!!
        )

        // then
        val notifications = notificationRepository.findAll().toList()
        assertEquals(4, notifications.size)
        assertTrue(notifications.all { it.messageType == NotificationType.JOINED_QUEUE })
        assertTrue(notifications.all { it.participantId == 1L })
        assertTrue(notifications.none { it.user?.id == 4L })
    }

    @Test
    @Sql(
        "/com/innopolis/innoqueue/domain/queue/service/impl/users.sql",
        "fcm_token.sql",
        "/com/innopolis/innoqueue/domain/queue/service/impl/queues.sql",
        "/com/innopolis/innoqueue/domain/queue/service/impl/user_queue.sql"
    )
    fun `Test sendNotificationMessage LEFT_QUEUE`() {
        // given
        val notificationType = NotificationType.LEFT_QUEUE
        val participantModel = getUser()
        val queueModel = getQueueModel(participantModel)

        // when
        notificationService.sendNotificationMessage(
            notificationType,
            participantModel.id!!,
            participantModel.name!!,
            queueModel.queueId!!,
            queueModel.name!!
        )

        // then
        val notifications = notificationRepository.findAll().toList()
        assertEquals(4, notifications.size)
        assertTrue(notifications.all { it.messageType == NotificationType.LEFT_QUEUE })
        assertTrue(notifications.all { it.participantId == 1L })
        assertTrue(notifications.none { it.user?.id == 4L })
    }

    @Test
    @Sql(
        "/com/innopolis/innoqueue/domain/queue/service/impl/users.sql",
        "fcm_token.sql",
        "/com/innopolis/innoqueue/domain/queue/service/impl/queues.sql",
        "/com/innopolis/innoqueue/domain/queue/service/impl/user_queue.sql"
    )
    fun `Test sendNotificationMessage DELETE_QUEUE`() {
        // given
        val notificationType = NotificationType.DELETE_QUEUE
        val participantModel = getUser()
        val queueModel = getQueueModel(participantModel)

        // when
        notificationService.sendNotificationMessage(
            notificationType,
            participantModel.id!!,
            participantModel.name!!,
            queueModel.queueId!!,
            queueModel.name!!
        )

        // then
        val notifications = notificationRepository.findAll().toList()
        assertEquals(5, notifications.size)
        assertTrue(notifications.all { it.messageType == NotificationType.DELETE_QUEUE })
        assertTrue(notifications.all { it.participantId == 1L })
    }

    private fun getQueueModel(participantModel: User): Queue {
        val queueModel = Queue().apply {
            queueId = 44L
            name = "Bring Water"
            color = "BLUE"
            creatorId = participantModel.id
            trackExpenses = false
            isImportant = false
            currentUserId = participantModel.id
        }
//        queueModel.userQueues = mutableSetOf(
//            UserQueue().apply {
//                userQueueId?.queueId = queueModel.id
//                userQueueId?.userId = participantModel.id
//                isActive = true
//                progress = 0
//                completes = 0
//                skips = 0
//                expenses = 0L
//                dateJoined = LocalDateTime.of(2022, 11, 4, 12, 12, 12)
//            },
//            getUserQueue(2L, "Emil", queueModel),
//            getUserQueue(3L, "Roman", queueModel),
//            getUserQueue(4L, "Timur", queueModel, false),
//            getUserQueue(5L, "Ivan", queueModel)
//        )
        return queueModel
    }

//    private fun getUserQueue(
//        userId: Long,
//        userName: String,
//        queueModel: Queue,
//        enableUserSettings: Boolean = true
//    ) =
//        UserQueue().apply {
//            userQueueId?.queueId = queueModel.id
//            userQueueId?.userId = userId
//            isActive = true
//            progress = 0
//            completes = 0
//            skips = 0
//            expenses = 0L
//            dateJoined = LocalDateTime.of(2022, 11, 4, 12, 12, 12)
//        }

    private fun getUser(userId: Long = 1L, userName: String = "admin", value: Boolean = true) = User().apply {
        id = userId
        name = userName
        completed = value
        skipped = value
        joinedQueue = value
        freeze = value
        leftQueue = value
        yourTurn = value
    }
}
