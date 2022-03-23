package com.innopolis.innoqueue.repository

import com.innopolis.innoqueue.model.User
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : CrudRepository<User, Long>