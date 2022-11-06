package com.innopolis.innoqueue.models

import javax.persistence.*

@Entity
@Table(name = "queue")
class Queue {
    @Id
    @SequenceGenerator(name = "queues_generator", sequenceName = "queue_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "queues_generator")
    @Column(name = "queue_id", nullable = false)
    var id: Long? = null

    @Column(name = "name", nullable = false, length = 64)
    var name: String? = null

    @Column(name = "color", nullable = false, length = 64)
    var color: String? = null

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "creator_id", nullable = false)
    var creator: User? = null

    @Column(name = "track_expenses", nullable = false)
    var trackExpenses: Boolean? = false

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "current_user_id", nullable = false)
    var currentUser: User? = null

    @OneToMany(mappedBy = "queue")
    var userQueues: MutableSet<UserQueue> = mutableSetOf()
}
