package com.innopolis.innoqueue.model

import javax.persistence.*

@Entity
@Table(name = "queue_qr_code")
open class QueueQrCode {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    open var id: Long? = null

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "queue_id", nullable = false)
    open var queue: Queue? = null

    @Column(name = "qr_code", nullable = false, length = 64)
    open var qrCode: String? = null
}
