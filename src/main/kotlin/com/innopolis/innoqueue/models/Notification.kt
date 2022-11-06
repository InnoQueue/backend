package com.innopolis.innoqueue.models

import com.innopolis.innoqueue.enums.NotificationsType
import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Table(name = "notifications")
class Notification {
    @Id
    @SequenceGenerator(name = "notifications_generator", sequenceName = "notifications_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "notifications_generator")
    @Column(name = "notification_id", nullable = false)
    var id: Long? = null

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    var user: User? = null

    @Column(name = "message_type", nullable = false, length = 32)
    @Enumerated(EnumType.STRING)
    var messageType: NotificationsType? = null

    @Column(name = "participant_id", nullable = false)
    var participantId: Long? = null

    @Column(name = "queue_id", nullable = false)
    var queueId: Long? = null

    @Column(name = "is_read", nullable = false)
    var isRead: Boolean? = false

    @Column(name = "date", nullable = false)
    var date: LocalDateTime? = null
}
