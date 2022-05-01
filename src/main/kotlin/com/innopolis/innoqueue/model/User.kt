package com.innopolis.innoqueue.model

import javax.persistence.*

@Entity
@Table(name = "\"user\"")
open class User {
    @Id
    @SequenceGenerator(name = "users_generator", sequenceName = "user_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "users_generator")
    @Column(name = "user_id", nullable = false)
    open var id: Long? = null

    @Column(name = "token", nullable = false)
    open var token: String? = null

    @Column(name = "name", nullable = false, length = 64)
    open var name: String? = null

    @OneToMany(mappedBy = "user")
    open var queues: MutableSet<UserQueue> = mutableSetOf()

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "user")
    open var settings: UserSetting? = null

    @OneToMany(mappedBy = "user")
    open var notifications: MutableSet<Notification> = mutableSetOf()

    @OneToMany(mappedBy = "creator")
    open var createdQueues: MutableSet<Queue> = mutableSetOf()

    @OneToMany(mappedBy = "currentUser")
    open var tasks: MutableSet<Queue> = mutableSetOf()
}