package com.innopolis.innoqueue.rest.v1

import com.innopolis.innoqueue.domain.external.service.DatabaseService
import com.innopolis.innoqueue.rest.v1.BackendController
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test

class BackendControllerTest {

    @Test
    fun `Test clearInviteCodes service called`() {
        // given
        val service = mockk<DatabaseService>(relaxed = true)
        val controller = BackendController(service)

        // when
        controller.clearExpiredInviteCodes()

        // then
        verify(exactly = 1) { service.clearExpiredInviteCodes() }
    }
}
