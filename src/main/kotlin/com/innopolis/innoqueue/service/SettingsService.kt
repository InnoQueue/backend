package com.innopolis.innoqueue.service

import com.innopolis.innoqueue.dto.SettingsDTO
import com.innopolis.innoqueue.repository.UserRepository
import com.innopolis.innoqueue.repository.UserSettingsRepository
import org.springframework.stereotype.Service

@Service
class SettingsService(
    private val userService: UserService,
    private val userRepository: UserRepository,
    private val settingsRepository: UserSettingsRepository
) {
    fun getSettings(token: Long): SettingsDTO {
        val user = userService.getUserByToken(token)
        val settings = user.settings!!
        return SettingsDTO(
            user.name!!,
            settings.n1!!,
            settings.n2!!,
            settings.n3!!,
            settings.n4!!,
            settings.n5!!,
        )
    }

    fun updateSettings(token: Long, settings: SettingsDTO): SettingsDTO {
        val user = userService.getUserByToken(token)
        user.name = settings.userName
        val newSavedUser = userRepository.save(user)
        val userSettings = newSavedUser.settings!!
        userSettings.n1 = settings.n1
        userSettings.n2 = settings.n2
        userSettings.n3 = settings.n3
        userSettings.n4 = settings.n4
        userSettings.n5 = settings.n5
        val updatedSettings = settingsRepository.save(userSettings)
        return SettingsDTO(
            newSavedUser.name!!,
            updatedSettings.n1!!,
            updatedSettings.n2!!,
            updatedSettings.n3!!,
            updatedSettings.n4!!,
            updatedSettings.n5!!
        )
    }
}
