package com.innopolis.innoqueue.rest.v1

import com.innopolis.innoqueue.dto.SettingsDTO
import com.innopolis.innoqueue.rest.v1.SettingsController
import com.innopolis.innoqueue.services.SettingsService
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test

class SettingsControllerTest {

    @Test
    fun `Test getSettings service called`() {
        // given
        val token = "token"
        val service = mockk<SettingsService>(relaxed = true)
        val controller = SettingsController(service)

        // when
        controller.getSettings(token)

        // then
        verify(exactly = 1) { service.getSettings(token) }
    }

    @Test
    fun `Test updateSettings service called`() {
        // given
        val token = "token"
        val settingsDTO = SettingsDTO(
            userName = "userName",
            completed = true,
            skipped = false,
            joinedQueue = true,
            freeze = false,
            leftQueue = true,
            yourTurn = false
        )
        val service = mockk<SettingsService>(relaxed = true)
        val controller = SettingsController(service)

        // when
        controller.updateSettings(token, settingsDTO)

        // then
        verify(exactly = 1) { service.updateSettings(token, settingsDTO) }
    }
}
