package com.innopolis.innoqueue.controller

import com.innopolis.innoqueue.service.DatabaseService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping
class DatabaseController(private val service: DatabaseService) {
    @ExceptionHandler(Exception::class)
    fun handleNotFound(e: Exception): ResponseEntity<String> =
        ResponseEntity("Database is reset by default data", HttpStatus.OK)

    @GetMapping("/reset")
    fun resetDB() = service.resetDB()
}
