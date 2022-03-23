package com.innopolis.innoqueue.model

import java.time.Instant
import javax.persistence.*

@Entity
@Table(name = "user_notifications")
open class UserNotification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id", nullable = false)
    open var id: Long? = null

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    open var user: User? = null

    @Column(name = "message", nullable = false, length = 256)
    open var message: String? = null

    @Column(name = "date", nullable = false)
    open var date: Instant? = null
}