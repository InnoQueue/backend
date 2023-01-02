package com.innopolis.innoqueue.model

import java.io.Serializable
import javax.persistence.Column
import javax.persistence.Embeddable

@Embeddable
class UserQueueId : Serializable {

    @Column(name = "user_id", nullable = false)
    var userId: Long? = null

    @Column(name = "queue_id", nullable = false)
    var queueId: Long? = null

//    @ManyToOne(fetch = FetchType.LAZY, optional = false)
//    @JoinColumn(name = "user_id", nullable = false)
//    var user: User? = null
//
//    @ManyToOne(fetch = FetchType.LAZY, optional = false)
//    @JoinColumn(name = "queue_id", nullable = false)
//    var queue: Queue? = null

    companion object {
        const val serialVersionUID = 2L
    }
}
