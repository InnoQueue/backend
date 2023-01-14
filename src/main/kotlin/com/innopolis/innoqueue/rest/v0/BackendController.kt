package com.innopolis.innoqueue.rest.v0

import com.innopolis.innoqueue.domain.external.dto.HostDto
import com.innopolis.innoqueue.domain.external.service.DatabaseService
import com.innopolis.innoqueue.rest.v0.dto.EmptyDto
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * Controller for managing Database
 */
@RestController
@RequestMapping
@Tag(
    name = "Backend Settings",
    description = "Requests to manipulate with the database"
)
class BackendController(private val service: DatabaseService) {

    /**
     * Endpoint for deleting expired invite codes
     */
    @Operation(
        summary = "Reset invite codes",
        description = "- Open this URL to delete expired invite codes forcibly.\n\n" +
                "- You don't need to provide any `user-token`"
    )
    @GetMapping("/clear")
    fun clearExpiredInviteCodes(): EmptyDto = service.clearExpiredInviteCodes()

    @Operation(
        summary = "Get up-to date hosts",
        description = "Returns host names which are recommended to use.\n\n" +
                "- `dev` - host for testing the app on non user real data.\n\n" +
                "- `prod` - host for backend which works with real user data."
    )
    @GetMapping("/host")
    fun getHost(): HostDto = service.getHost()
}
