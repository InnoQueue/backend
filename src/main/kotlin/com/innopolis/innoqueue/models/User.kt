package com.innopolis.innoqueue.models

import javax.persistence.*

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

    @OneToMany(mappedBy = "user")
    var queues: MutableSet<UserQueue> = mutableSetOf()

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "user")
    var settings: UserSettings? = null

    @OneToMany(mappedBy = "user")
    var notifications: MutableSet<Notification> = mutableSetOf()

    @OneToMany(mappedBy = "creator")
    var createdQueues: MutableSet<Queue> = mutableSetOf()

    @OneToMany(mappedBy = "currentUser")
    var tasks: MutableSet<Queue> = mutableSetOf()
}
