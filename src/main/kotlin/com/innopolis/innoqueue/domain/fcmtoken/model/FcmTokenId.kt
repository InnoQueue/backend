package com.innopolis.innoqueue.domain.fcmtoken.model

import java.io.Serializable
import javax.persistence.Column
import javax.persistence.Embeddable

@Embeddable
class FcmTokenId : Serializable {

    @Column(name = "user_id", nullable = false)
    var userId: Long? = null

    @Column(name = "fcm_token", nullable = false, length = 256)
    var fcmToken: String? = null

    companion object {
        const val serialVersionUID = 1L
    }
}
