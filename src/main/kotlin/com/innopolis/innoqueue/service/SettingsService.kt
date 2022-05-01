package com.innopolis.innoqueue.service

import com.innopolis.innoqueue.dto.SettingsDTO
import com.innopolis.innoqueue.model.User
import com.innopolis.innoqueue.repository.UserRepository
import com.innopolis.innoqueue.repository.UserSettingsRepository
import org.springframework.stereotype.Service

@Service
class SettingsService(
    private val userService: UserService,
    private val userRepository: UserRepository,
    private val settingsRepository: UserSettingsRepository
) {
    fun getSettings(token: String): SettingsDTO {
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

    fun updateSettings(token: String, settings: SettingsDTO): SettingsDTO {
        val user = userService.getUserByToken(token)
        var (newSavedUser, changed) = updateUserNameOrDefault(user, settings)

        val userSettings = newSavedUser.settings!!
        if (settings.n1 != null) {
            userSettings.n1 = settings.n1
            changed = true
        }
        if (settings.n2 != null) {
            userSettings.n2 = settings.n2
            changed = true
        }
        if (settings.n3 != null) {
            userSettings.n3 = settings.n3
            changed = true
        }
        if (settings.n4 != null) {
            userSettings.n4 = settings.n4
            changed = true
        }
        if (settings.n5 != null) {
            userSettings.n5 = settings.n5
            changed = true
        }
        val updatedSettings = when (changed) {
            true -> {
                settingsRepository.save(userSettings)
            }
            false -> userSettings
        }
        return SettingsDTO(
            newSavedUser.name!!,
            updatedSettings.n1!!,
            updatedSettings.n2!!,
            updatedSettings.n3!!,
            updatedSettings.n4!!,
            updatedSettings.n5!!
        )
    }

    private fun updateUserNameOrDefault(user: User, settings: SettingsDTO): Pair<User, Boolean> {
        return if (settings.userName != null) {
            if (settings.userName.isEmpty()) {
                throw IllegalArgumentException("Username can't be an empty string")
            }
            user.name = settings.userName
            userRepository.save(user) to true
        } else {
            user to false
        }
    }
}
