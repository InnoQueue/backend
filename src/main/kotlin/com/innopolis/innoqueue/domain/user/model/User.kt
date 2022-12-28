package com.innopolis.innoqueue.domain.user.model

import com.innopolis.innoqueue.models.Notification
import com.innopolis.innoqueue.models.UserQueue
import javax.persistence.*

/**
 * Model representing the "user" db table
 */
@Entity
@Table(name = "\"user\"")
class User {
    @Id
    @SequenceGenerator(name = "users_generator", sequenceName = "user_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "users_generator")
    @Column(name = "user_id", nullable = false)
    var id: Long? = null

    @Column(name = "token", nullable = false)
    var token: String? = null

    @Column(name = "name", nullable = false, length = 64)
    var name: String? = null

    @Column(name = "fcm_token", nullable = false, length = 256)
    var fcmToken: String? = null

    @Column(name = "completed", nullable = false)
    var completed: Boolean? = true

    @Column(name = "skipped", nullable = false)
    var skipped: Boolean? = true

    @Column(name = "joined_queue", nullable = false)
    var joinedQueue: Boolean? = true

    @Column(name = "\"freeze\"", nullable = false)
    var freeze: Boolean? = true

    @Column(name = "left_queue", nullable = false)
    var leftQueue: Boolean? = true

    @Column(name = "your_turn", nullable = false)
    var yourTurn: Boolean? = true

    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER)
    var queues: MutableSet<UserQueue> = mutableSetOf()

    @OneToMany(mappedBy = "user")
    var notifications: MutableSet<Notification> = mutableSetOf()
}
