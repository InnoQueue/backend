package com.innopolis.innoqueue.services

import com.innopolis.innoqueue.dao.UserRepository
import com.innopolis.innoqueue.dao.UserSettingsRepository
import com.innopolis.innoqueue.dto.SettingsDTO
import com.innopolis.innoqueue.models.User
import com.innopolis.innoqueue.models.UserSettings
import org.springframework.stereotype.Service

@Service
class SettingsService(
    private val userService: UserService,
    private val userRepository: UserRepository,
    private val settingsRepository: UserSettingsRepository
) {
    fun getSettings(token: String): SettingsDTO {
        val user = userService.findUserByToken(token)
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
        val user = userService.findUserByToken(token)
        var (newSavedUser, shouldSave) = user.updateUserName(settings)
        val userSettings = settings.updateUserSettings(newSavedUser.settings!!)
        shouldSave = when (settings.anyNewSettingsChange()) {
            true -> true
            false -> shouldSave
        }
        return when (shouldSave) {
            true -> {
                settingsRepository.save(userSettings)
            }

            false -> userSettings
        }.let {
            SettingsDTO(
                newSavedUser.name!!,
                it.completed!!,
                it.skipped!!,
                it.joinedQueue!!,
                it.freeze!!,
                it.leftQueue!!,
                it.yourTurn!!
            )
        }
    }

    private fun User.updateUserName(settings: SettingsDTO): Pair<User, Boolean> =
        settings.userName?.let {
            if (it.isEmpty()) {
                throw IllegalArgumentException("Username can't be an empty string")
            }
            this.name = it
            userRepository.save(this) to true
        } ?: (this to false)

    private fun SettingsDTO.updateUserSettings(userSettings: UserSettings): UserSettings {
        this.completed?.let {
            userSettings.completed = this.completed
        }
        this.skipped?.let {
            userSettings.skipped = this.skipped
        }
        this.joinedQueue?.let {
            userSettings.joinedQueue = this.joinedQueue
        }
        this.freeze?.let {
            userSettings.freeze = this.freeze
        }
        this.leftQueue?.let {
            userSettings.leftQueue = this.leftQueue
        }
        this.yourTurn?.let {
            userSettings.yourTurn = this.yourTurn
        }
        return userSettings
    }

    private fun SettingsDTO.anyNewSettingsChange() =
        this.completed != null ||
                this.skipped != null ||
                this.joinedQueue != null ||
                this.freeze != null ||
                this.leftQueue != null ||
                this.yourTurn != null
}
