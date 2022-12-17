package com.innopolis.innoqueue.dao

import com.innopolis.innoqueue.models.UserSettings
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

/**
 * DAO repository for working with "user_settings" db table
 */
@Repository
interface UserSettingsRepository : CrudRepository<UserSettings, Long>
