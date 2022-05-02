package com.innopolis.innoqueue.model

import java.time.LocalDate
import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Table(name = "notifications")
open class Notification {
    @Id
    @SequenceGenerator(name = "notifications_generator", sequenceName = "notifications_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "notifications_generator")
    @Column(name = "notification_id", nullable = false)
    open var id: Long? = null

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    open var user: User? = null

    @Column(name = "message_type", nullable = false, length = 32)
    open var messageType: String? = null

    @Column(name = "participant_id", nullable = false)
    open var participantId: Long? = null

    @Column(name = "queue_id", nullable = false)
    open var queueId: Long? = null

    @Column(name = "is_read", nullable = false)
    open var isRead: Boolean? = false

    @Column(name = "date", nullable = false)
    open var date: LocalDateTime? = null
}
