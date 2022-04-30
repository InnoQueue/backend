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

    @Column(name = "n1", nullable = false)
    open var n1: Boolean? = true

    @Column(name = "n2", nullable = false)
    open var n2: Boolean? = true

    @Column(name = "n3", nullable = false)
    open var n3: Boolean? = true

    @Column(name = "n4", nullable = false)
    open var n4: Boolean? = true

    @Column(name = "n5", nullable = false)
    open var n5: Boolean? = true
}