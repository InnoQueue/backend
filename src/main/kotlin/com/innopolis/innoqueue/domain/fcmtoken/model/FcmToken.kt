package com.innopolis.innoqueue.domain.fcmtoken.model

import java.time.LocalDateTime
import javax.persistence.Column
import javax.persistence.EmbeddedId
import javax.persistence.Entity
import javax.persistence.Table

/**
 * Model representing the "fcm_token" db table
 */
@Entity
@Table(name = "fcm_token")
class FcmToken {

    @EmbeddedId
    var fcmTokenId: FcmTokenId? = null

    @Column(name = "date_created", nullable = false)
    var dateCreated: LocalDateTime? = null
}
