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
            settings.completed!!,
            settings.skipped!!,
            settings.joinedQueue!!,
            settings.freeze!!,
            settings.leftQueue!!,
            settings.yourTurn!!
        )
    }

    fun updateSettings(token: String, settings: SettingsDTO): SettingsDTO {
        val user = userService.getUserByToken(token)
        var (newSavedUser, changed) = updateUserNameOrDefault(user, settings)

        val userSettings = newSavedUser.settings!!
        if (settings.completed != null) {
            userSettings.completed = settings.completed
            changed = true
        }
        if (settings.skipped != null) {
            userSettings.skipped = settings.skipped
            changed = true
        }
        if (settings.joinedQueue != null) {
            userSettings.joinedQueue = settings.joinedQueue
            changed = true
        }
        if (settings.freeze != null) {
            userSettings.freeze = settings.freeze
            changed = true
        }
        if (settings.leftQueue != null) {
            userSettings.leftQueue = settings.leftQueue
            changed = true
        }
        if (settings.yourTurn != null) {
            userSettings.yourTurn = settings.yourTurn
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
            updatedSettings.completed!!,
            updatedSettings.skipped!!,
            updatedSettings.joinedQueue!!,
            updatedSettings.freeze!!,
            updatedSettings.leftQueue!!,
            updatedSettings.yourTurn!!
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
