package com.innopolis.innoqueue.model

import java.time.LocalDateTime
import javax.persistence.Column
import javax.persistence.EmbeddedId
import javax.persistence.Entity
import javax.persistence.Table

/**
 * Model representing the "user_queue" db table
 */
@Entity
@Table(name = "user_queue")
class UserQueue {
    @EmbeddedId
    var userQueueId: UserQueueId? = null

    @Column(name = "progress", nullable = false)
    var progress: Int? = 0

    @Column(name = "completes", nullable = false)
    var completes: Int? = 0

    @Column(name = "skips", nullable = false)
    var skips: Int? = 0

    @Column(name = "expenses", nullable = false)
    var expenses: Long? = 0

    @Column(name = "is_active", nullable = false)
    var isActive: Boolean? = true

    @Column(name = "date_joined", nullable = false)
    var dateJoined: LocalDateTime? = null
}
