package com.innopolis.innoqueue.rest.v1

import com.innopolis.innoqueue.rest.v1.dto.EmptyDTO
import com.innopolis.innoqueue.services.DatabaseService
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping
@Tag(
    name = "Backend Settings",
    description = "Requests to manipulate with the database"
)
class DatabaseController(private val service: DatabaseService) {

    @GetMapping("/clear")
    fun clearExpiredInviteCodes(): EmptyDTO = service.clearExpiredInviteCodes()
}
