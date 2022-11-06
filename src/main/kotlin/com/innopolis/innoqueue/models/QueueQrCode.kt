package com.innopolis.innoqueue.models

import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Table(name = "queue_qr_code")
class QueueQrCode {
    @Id
    @SequenceGenerator(name = "queue_qr_codes_generator", sequenceName = "queue_qr_code2_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "queue_qr_codes_generator")
    @Column(name = "id", nullable = false)
    var id: Long? = null

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "queue_id", nullable = false)
    var queue: Queue? = null

    @Column(name = "qr_code", nullable = false, length = 64)
    var qrCode: String? = null

    @Column(name = "date_created", nullable = false)
    var dateCreated: LocalDateTime? = null
}
