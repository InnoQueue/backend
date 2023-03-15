package com.innopolis.innoqueue.domain.user.dao

import com.innopolis.innoqueue.domain.user.model.User
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

/**
 * DAO repository for working with "user" db table
 */
@Repository
interface UserRepository : CrudRepository<User, Long> {
    /**
     * Find user model by its token
     * @param token - user token
     */
    fun findUserByToken(token: String): User?
}
