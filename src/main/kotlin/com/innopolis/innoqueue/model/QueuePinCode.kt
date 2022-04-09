package com.innopolis.innoqueue.model

import javax.persistence.*

@Entity
@Table(name = "queue_pin_code")
open class QueuePinCode {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    open var id: Long? = null

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "queue_id", nullable = false)
    open var queue: Queue? = null

    @Column(name = "pin_code", nullable = false, length = 8)
    open var pinCode: String? = null
}