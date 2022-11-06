package com.innopolis.innoqueue.models

import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Table(name = "queue_pin_code")
class QueuePinCode {
    @Id
    @SequenceGenerator(name = "queue_pin_codes_generator", sequenceName = "queue_pin_code2_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "queue_pin_codes_generator")
    @Column(name = "id", nullable = false)
    var id: Long? = null

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "queue_id", nullable = false)
    var queue: Queue? = null

    @Column(name = "pin_code", nullable = false, length = 8)
    var pinCode: String? = null

    @Column(name = "date_created", nullable = false)
    var dateCreated: LocalDateTime? = null
}
