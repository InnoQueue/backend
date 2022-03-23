package com.innopolis.innoqueue.repository

import com.innopolis.innoqueue.model.Queue
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository

interface QueueRepository : CrudRepository<Queue, Long>