package com.innopolis.innoqueue.model

import javax.persistence.*

@Entity
@Table(name = "queue_pin_code")
open class QueuePinCode {
    @Id
    @SequenceGenerator(name = "queue_pin_codes_generator", sequenceName = "queue_pin_code2_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "queue_pin_codes_generator")
    @Column(name = "id", nullable = false)
    open var id: Long? = null

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "queue_id", nullable = false)
    open var queue: Queue? = null

    @Column(name = "pin_code", nullable = false, length = 8)
    open var pinCode: String? = null
}