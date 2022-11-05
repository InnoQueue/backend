package com.innopolis.innoqueue.controller

import com.innopolis.innoqueue.dto.SettingsDTO
import com.innopolis.innoqueue.service.SettingsService
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test

class SettingsControllerTest {

    @Test
    fun `Test getSettings service called`() {
        // given
        val token = "123"
        val settingsService = mockk<SettingsService>(relaxed = true)
        val settingsController = SettingsController(settingsService)

        // when
        settingsController.getSettings(token)

        // then
        verify(exactly = 1) { settingsService.getSettings(token) }
    }

    @Test
    fun `Test updateSettings service called`() {
        // given
        val token = "123"
        val settingsDTO = SettingsDTO(
            userName = "userName",
            completed = true,
            skipped = false,
            joinedQueue = true,
            freeze = false,
            leftQueue = true,
            yourTurn = false
        )
        val settingsService = mockk<SettingsService>(relaxed = true)
        val settingsController = SettingsController(settingsService)

        // when
        settingsController.updateSettings(token, settingsDTO)

        // then
        verify(exactly = 1) { settingsService.updateSettings(token, settingsDTO) }
    }
}
