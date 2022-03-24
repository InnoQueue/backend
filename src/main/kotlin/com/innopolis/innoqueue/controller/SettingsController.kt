package com.innopolis.innoqueue.controller

import com.innopolis.innoqueue.dto.SettingsDTO
import com.innopolis.innoqueue.service.SettingsService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/settings")
class SettingsController(private val service: SettingsService) {

    @ExceptionHandler(NoSuchElementException::class)
    fun handleNotFound(e: NoSuchElementException): ResponseEntity<String> =
        ResponseEntity(e.message, HttpStatus.NOT_FOUND)

    @GetMapping
    fun getSettings(@RequestHeader("user-token") token: String): SettingsDTO = service.getSettings(token)

    @PatchMapping
    @ResponseStatus(HttpStatus.OK)
    fun updateSettings(@RequestHeader("user-token") token: String, @RequestBody settings: SettingsDTO): SettingsDTO =
        service.updateSettings(token, settings)
}
