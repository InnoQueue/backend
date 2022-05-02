package com.innopolis.innoqueue.model

import javax.persistence.*

@Entity
@Table(name = "user_settings")
open class UserSetting {
    @Id
    @SequenceGenerator(name = "user_settings_generator", sequenceName = "user_settings_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_settings_generator")
    @Column(name = "user_settings_id", nullable = false)
    open var id: Long? = null

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    open var user: User? = null

    @Column(name = "completed", nullable = false)
    open var completed: Boolean? = true

    @Column(name = "skipped", nullable = false)
    open var skipped: Boolean? = true

    @Column(name = "joined_queue", nullable = false)
    open var joinedQueue: Boolean? = true

    @Column(name = "\"freeze\"", nullable = false)
    open var freeze: Boolean? = true

    @Column(name = "left_queue", nullable = false)
    open var leftQueue: Boolean? = true

    @Column(name = "your_turn", nullable = false)
    open var yourTurn: Boolean? = true
}
