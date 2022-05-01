package com.innopolis.innoqueue.repository

import com.innopolis.innoqueue.model.Notification
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository

interface NotificationRepository : CrudRepository<Notification, Long>