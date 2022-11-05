package com.innopolis.innoqueue.repository

import com.innopolis.innoqueue.model.UserSettings
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository

interface UserSettingsRepository : CrudRepository<UserSettings, Long>
