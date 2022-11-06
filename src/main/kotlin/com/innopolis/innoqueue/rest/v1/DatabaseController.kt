package com.innopolis.innoqueue.rest.v1

import com.innopolis.innoqueue.rest.v1.dto.EmptyDTO
import com.innopolis.innoqueue.services.DatabaseService
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
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

    // TODO remove handleNotFound method and handle exceptions in an appropriate way
    @ExceptionHandler(Exception::class)
    @Suppress("UnusedPrivateMember")
    fun handleNotFound(e: Exception): ResponseEntity<String> =
        ResponseEntity("Database is reset by default data", HttpStatus.OK)

//    @GetMapping("/reset")
//    fun resetDB(): EmptyDTO = service.resetDB()

    @GetMapping("/clear")
    fun clearCodes(): EmptyDTO = service.clearCodes()
}
