package com.innopolis.innoqueue.rest.v1

import com.innopolis.innoqueue.dto.NotificationsListDTO
import com.innopolis.innoqueue.rest.v1.dto.EmptyDTO
import com.innopolis.innoqueue.rest.v1.dto.NewNotificationDTO
import com.innopolis.innoqueue.service.NotificationsService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

/**
 * Controller with endpoints to work with notifications
 */
@RestController
@RequestMapping("/notifications")
@Tag(name = "Notifications")
class NotificationsController(
    private val notificationsService: NotificationsService
) {

    /**
     * Exception handler
     */
    @ExceptionHandler(NoSuchElementException::class)
    fun handleNotFound(e: NoSuchElementException): ResponseEntity<String> =
        ResponseEntity(e.message, HttpStatus.NOT_FOUND)

    /**
     * GET endpoint for listing all notifications
     * @param token - user token
     */
    @Operation(
        summary = "Get notifications",
        description = "The server stores notifications for **2 weeks only**.\n\n" +
                "Next time when you send this request, all `unread` messages will be in the `all` section."
    )
    @GetMapping
    fun getNotifications(@RequestHeader("user-token") token: String): NotificationsListDTO =
        notificationsService.getNotifications(token)

    /**
     * GET endpoint for indicating whether there is any unread notification
     * @param token - user token
     */
    @Operation(
        summary = "Check for any new notification",
        description = "You can check whether a client has any unread " +
                "notification message via this endpoint.\n\n" +
                "- `true` - the client has at least one `unread` notification.\n\n" +
                "- `false` - the client does not have any `unread` notification.\n\n" +
                "You can send this request any number of times: the `unread` message won't become `read`."
    )
    @GetMapping("/new")
    fun anyNewNotification(@RequestHeader("user-token") token: String): NewNotificationDTO =
        notificationsService.anyNewNotification(token)

    /**
     * GET endpoint for deleting notifications older than 2 weeks
     */
    @Operation(
        summary = "Clear old notifications",
        description = "- Open this URL to delete old notifications forcibly.\n\n" +
                "- You don't need to provide any `user-token`.\n\n" +
                "- `old notifications` - notifications that are older than **2 weeks**.\n\n"
    )
    @GetMapping("/clear")
    fun clearOldNotifications(): EmptyDTO = notificationsService.clearOldNotifications()
}
