package com.innopolis.innoqueue.controller

import com.innopolis.innoqueue.dto.NotificationsListDTO
import com.innopolis.innoqueue.service.NotificationsService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/notifications")
class NotificationsController(private val service: NotificationsService) {

    @ExceptionHandler(NoSuchElementException::class)
    fun handleNotFound(e: NoSuchElementException): ResponseEntity<String> =
        ResponseEntity(e.message, HttpStatus.NOT_FOUND)

    @GetMapping
    fun getNotifications(@RequestHeader("user-token") token: Long): NotificationsListDTO =
        service.getNotifications(token)
}