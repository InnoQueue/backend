package com.innopolis.innoqueue.models

import javax.persistence.*

@Entity
@Table(name = "user_settings")
class UserSettings {
    @Id
    @SequenceGenerator(name = "user_settings_generator", sequenceName = "user_settings_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_settings_generator")
    @Column(name = "user_settings_id", nullable = false)
    var id: Long? = null

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    var user: User? = null

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
}
