package com.innopolis.innoqueue.repository

import com.innopolis.innoqueue.model.UserQueue
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository

interface UserQueueRepository : CrudRepository<UserQueue, Long>