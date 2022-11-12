package com.innopolis.innoqueue.rest.v1

import com.innopolis.innoqueue.services.DatabaseService
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test

class DatabaseControllerTest {

    @Test
    fun `Test clearInviteCodes service called`() {
        // given
        val service = mockk<DatabaseService>(relaxed = true)
        val controller = DatabaseController(service)

        // when
        controller.clearExpiredInviteCodes()

        // then
        verify(exactly = 1) { service.clearExpiredInviteCodes() }
    }
}
