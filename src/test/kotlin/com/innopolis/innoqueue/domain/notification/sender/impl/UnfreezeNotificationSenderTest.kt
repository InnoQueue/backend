package com.innopolis.innoqueue.domain.notification.sender.impl

import com.innopolis.innoqueue.domain.notification.dao.NotificationRepository
import com.innopolis.innoqueue.domain.notification.dto.NotificationMessageDto
import com.innopolis.innoqueue.domain.notification.enums.NotificationType
import com.innopolis.innoqueue.domain.queue.model.Queue
import com.innopolis.innoqueue.domain.user.model.User
import com.innopolis.innoqueue.testcontainer.PostgresTestContainer
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.jdbc.Sql

class UnfreezeNotificationSenderTest : PostgresTestContainer() {

    @Autowired
    private lateinit var notificationSenderService: UnfreezeNotificationSender

    @Autowired
    private lateinit var notificationRepository: NotificationRepository

    @Test
    @Sql(
        "/com/innopolis/innoqueue/domain/queue/service/impl/users.sql",
        "/com/innopolis/innoqueue/domain/notification/service/impl/fcm_token.sql",
        "/com/innopolis/innoqueue/domain/queue/service/impl/queues.sql",
        "/com/innopolis/innoqueue/domain/queue/service/impl/user_queue.sql"
    )
    fun `Test sendNotificationMessage UNFROZEN`() {
        // given
        val participantModel = User().apply {
            id = 1L
            name = "admin"
            completed = true
            skipped = true
            joinedQueue = true
            freeze = true
            leftQueue = true
            yourTurn = true
        }
        val queueModel = Queue().apply {
            queueId = 44L
            name = "Bring Water"
            color = "BLUE"
            creatorId = participantModel.id
            trackExpenses = false
            isImportant = false
            currentUserId = participantModel.id
        }

        // when
        notificationSenderService.sendNotificationMessage(
            NotificationMessageDto(
                participantId = participantModel.id!!,
                participantName = participantModel.name!!,
                queueId = queueModel.queueId!!,
                queueName = queueModel.name!!
            )
        )

        // then
        val notifications = notificationRepository.findAll().toList()
        assertEquals(4, notifications.size)
        assertTrue(notifications.all { it.messageType == NotificationType.UNFROZEN })
        assertTrue(notifications.all { it.participantId == 1L })
        assertTrue(notifications.none { it.user?.id == 4L })
    }
}
