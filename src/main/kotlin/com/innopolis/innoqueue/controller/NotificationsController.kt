package com.innopolis.innoqueue.controller

import com.innopolis.innoqueue.controller.dto.EmptyDTO
import com.innopolis.innoqueue.controller.dto.NewNotificationDTO
import com.innopolis.innoqueue.dto.NotificationsListDTO
import com.innopolis.innoqueue.service.NotificationsService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/notifications")
class NotificationsController(private val notificationsService: NotificationsService) {

    @ExceptionHandler(NoSuchElementException::class)
    fun handleNotFound(e: NoSuchElementException): ResponseEntity<String> =
        ResponseEntity(e.message, HttpStatus.NOT_FOUND)

    @GetMapping
    fun getNotifications(@RequestHeader("user-token") token: String): NotificationsListDTO =
        notificationsService.getNotifications(token)

    @GetMapping("/new")
    fun anyNewNotification(@RequestHeader("user-token") token: String): NewNotificationDTO =
        notificationsService.anyNewNotification(token)

    @GetMapping("/clear")
    fun clearOldNotifications(): EmptyDTO = notificationsService.clearOldNotifications()
}
