package com.innopolis.innoqueue.repository

import com.innopolis.innoqueue.model.UserNotification
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository

interface UserNotificationsRepository : CrudRepository<UserNotification, Long>