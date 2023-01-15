package com.innopolis.innoqueue.rest.v1

import com.innopolis.innoqueue.domain.external.dto.HostDto
import com.innopolis.innoqueue.domain.external.service.DatabaseService
import com.innopolis.innoqueue.rest.v0.dto.EmptyDto
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * Controller for managing Database
 */
@RestController
@RequestMapping("/api/v1")
@Tag(
    name = "Backend Settings",
    description = "Requests to manipulate with the Backend and the Database"
)
class BackendController(private val service: DatabaseService) {

    /**
     * Endpoint for deleting expired invite codes
     */
    @Operation(
        summary = "Reset invite QR and PIN codes",
        description = "- Delete expired invite codes which are older than 2 weeks forcibly."
    )
    @PostMapping("/invitations/clear")
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
