package com.innopolis.innoqueue.dao

import com.innopolis.innoqueue.models.User
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : CrudRepository<User, Long> {
    fun findUserByToken(token: String): User?
}
