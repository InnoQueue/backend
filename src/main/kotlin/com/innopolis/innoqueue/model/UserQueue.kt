package com.innopolis.innoqueue.model

import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Table(name = "user_queue")
open class UserQueue {
    @Id
    @SequenceGenerator(name = "user_queues_generator", sequenceName = "user_queue_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_queues_generator")
    @Column(name = "user_queue_id", nullable = false)
    open var id: Long? = null

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "queue_id", nullable = false)
    open var queue: Queue? = null

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    open var user: User? = null

    @Column(name = "is_active", nullable = false)
    open var isActive: Boolean? = false

    @Column(name = "skips", nullable = false)
    open var skips: Int? = null

    @Column(name = "expenses", nullable = false)
    open var expenses: Double? = null

    @Column(name = "is_important", nullable = false)
    open var isImportant: Boolean? = false

    @Column(name = "date_joined", nullable = false)
    open var dateJoined: LocalDateTime? = null
}