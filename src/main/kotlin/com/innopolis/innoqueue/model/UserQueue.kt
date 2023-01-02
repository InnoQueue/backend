package com.innopolis.innoqueue.model

import com.innopolis.innoqueue.domain.queue.model.Queue
import com.innopolis.innoqueue.domain.user.model.User
import java.time.LocalDateTime
import javax.persistence.*

/**
 * Model representing the "user_queue" db table
 */
@Entity
@Table(name = "user_queue")
class UserQueue {
    @Id
    @SequenceGenerator(name = "user_queues_generator", sequenceName = "user_queue_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_queues_generator")
    @Column(name = "user_queue_id", nullable = false)
    var id: Long? = null

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "queue_id", nullable = false)
    var queue: Queue? = null

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    var user: User? = null

    @Column(name = "is_active", nullable = false)
    var isActive: Boolean? = true

    @Column(name = "progress", nullable = false)
    var progress: Int? = 0

    @Column(name = "completes", nullable = false)
    var completes: Int? = 0

    @Column(name = "skips", nullable = false)
    var skips: Int? = 0

    @Column(name = "expenses", nullable = false)
    var expenses: Double? = 0.0

    @Column(name = "date_joined", nullable = false)
    var dateJoined: LocalDateTime? = null
}
