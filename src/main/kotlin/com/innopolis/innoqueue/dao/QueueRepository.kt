package com.innopolis.innoqueue.dao

import com.innopolis.innoqueue.models.Queue
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository

interface QueueRepository : CrudRepository<Queue, Long>
