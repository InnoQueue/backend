package com.innopolis.innoqueue.domain.queue.model

import com.innopolis.innoqueue.domain.user.model.User
import com.innopolis.innoqueue.model.UserQueue
import java.time.LocalDateTime
import javax.persistence.*

/**
 * Model representing the "queue" db table
 */
@Entity
@Table(name = "queue")
class Queue {
    @Id
    @SequenceGenerator(name = "queues_generator", sequenceName = "queue_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "queues_generator")
    @Column(name = "queue_id", nullable = false)
    var id: Long? = null

    @Column(name = "name", nullable = false, length = 64)
    var name: String? = null

    @Column(name = "color", nullable = false, length = 64)
    var color: String? = null

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "creator_id", nullable = false)
    var creator: User? = null

    @Column(name = "track_expenses", nullable = false)
    var trackExpenses: Boolean? = false

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "current_user_id", nullable = false)
    var currentUser: User? = null

    @Column(name = "pin_code", nullable = true, length = 8)
    var pinCode: String? = null

    @Column(name = "qr_code", nullable = true, length = 64)
    var qrCode: String? = null

    @Column(name = "pin_date_created", nullable = true)
    var pinDateCreated: LocalDateTime? = null

    @Column(name = "qr_date_created", nullable = true)
    var qrDateCreated: LocalDateTime? = null

    @Column(name = "is_important", nullable = false)
    var isImportant: Boolean? = false

    @OneToMany(mappedBy = "queue", fetch = FetchType.EAGER)
    var userQueues: MutableSet<UserQueue> = mutableSetOf()
}
