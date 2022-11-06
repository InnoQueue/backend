package com.innopolis.innoqueue.dao

import com.innopolis.innoqueue.models.UserQueue
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository

interface UserQueueRepository : CrudRepository<UserQueue, Long>
