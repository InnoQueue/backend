package com.innopolis.innoqueue.model

import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Table(name = "queue_qr_code")
open class QueueQrCode {
    @Id
    @SequenceGenerator(name = "queue_qr_codes_generator", sequenceName = "queue_qr_code2_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "queue_qr_codes_generator")
    @Column(name = "id", nullable = false)
    open var id: Long? = null

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "queue_id", nullable = false)
    open var queue: Queue? = null

    @Column(name = "qr_code", nullable = false, length = 64)
    open var qrCode: String? = null

    @Column(name = "date_created", nullable = false)
    open var dateCreated: LocalDateTime? = null
}
