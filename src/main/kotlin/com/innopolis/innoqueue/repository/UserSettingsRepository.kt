package com.innopolis.innoqueue.repository

import com.innopolis.innoqueue.model.UserSetting
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository

interface UserSettingsRepository : CrudRepository<UserSetting, Long>