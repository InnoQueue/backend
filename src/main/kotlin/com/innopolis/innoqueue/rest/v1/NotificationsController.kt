package com.innopolis.innoqueue.rest.v1

import com.innopolis.innoqueue.domain.notification.dto.NotificationDto
import com.innopolis.innoqueue.domain.notification.service.NotificationService
import com.innopolis.innoqueue.rest.v1.dto.NewNotificationDto
import com.innopolis.innoqueue.rest.v1.dto.NotificationIdsDto
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

/**
 * Controller with endpoints to work with notifications
 */
@RestController
@RequestMapping("/api/v1/notifications")
@Tag(name = "Notifications")
class NotificationsController(
    private val notificationService: NotificationService
) {
    /**
     * Exception handler
     */
    @ExceptionHandler(NoSuchElementException::class)
    fun handleNotFound(e: NoSuchElementException): ResponseEntity<String> =
        ResponseEntity(e.message, HttpStatus.NOT_FOUND)

    /**
     * Exception handler
     */
    @ExceptionHandler(IllegalArgumentException::class)
    fun handleNotFound(e: IllegalArgumentException): ResponseEntity<String> =
        ResponseEntity(e.message, HttpStatus.BAD_REQUEST)

    /**
     * GET endpoint for listing all notifications
     * @param token - user token
     */
    @Operation(
        summary = "Get paginated list of notifications",
        description = "Use `page` and `size` to iterate through the list.\n\n." +
                "- `page` - number of page >= 0.\n\n." +
                "- `size` - number of elements per page. size > 0."
    )
    @GetMapping
    fun getNotifications(@RequestHeader("user-token") token: String, page: Int, size: Int): Page<NotificationDto> {
        validatePaginationArgs(page, size)
        val pageable: Pageable = PageRequest.of(
            page,
            size,
            Sort.by("is_read").and(Sort.by("date").descending())
        )
        return notificationService.getNotifications(token, pageable)
    }

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
    fun anyNewNotification(@RequestHeader("user-token") token: String): NewNotificationDto =
        notificationService.anyNewNotification(token)

//    /**
//     * GET endpoint for deleting notifications older than 2 weeks
//     */
//    @Operation(
//        summary = "Clear old notifications",
//        description = "- Open this URL to delete old notifications forcibly.\n\n" +
//                "- You don't need to provide any `user-token`.\n\n" +
//                "- `old notifications` - notifications that are older than **2 weeks**.\n\n"
//    )
//    @GetMapping("/clear")
//    fun clearOldNotifications(): EmptyDto = notificationService.clearOldNotifications()

    /**
     * POST endpoint for marking notifications as read
     */
    @Operation(
        summary = "Read notifications",
        description = "Specify notification ids which should be marked as read.\n\n" +
                "- You can specify ids in `notificationIds`. Then only these notifications will be read.\n\n" +
                "- You can specify nothing. Then **all** notifications will be marked as read.\n\n"
    )
    @PostMapping
    fun readNotifications(
        @RequestHeader("user-token") token: String,
        @RequestBody notificationIds: NotificationIdsDto?
    ) {
        notificationService.readNotifications(token, notificationIds?.notificationIds)
    }

    /**
     * POST endpoint for deleting notifications
     */
    @Operation(
        summary = "Delete notifications",
        description = "Specify notification ids which should be deleted.\n\n" +
                "- You can specify ids in `notificationIds`. Then only these notifications will be deleted.\n\n" +
                "- You can specify nothing. Then **all** notifications will be deleted.\n\n"
    )
    @PostMapping("/delete")
    fun deleteNotifications(
        @RequestHeader("user-token") token: String,
        @RequestBody notificationIds: NotificationIdsDto?
    ) {
        notificationService.deleteNotifications(token, notificationIds?.notificationIds)
    }

    /**
     * DELETE endpoint for deleting specified notification
     */
    @Operation(
        summary = "Delete notification by id",
        description = "Specify notification id which should be deleted."
    )
    @DeleteMapping("{notificationId}")
    fun deleteNotificationById(
        @RequestHeader("user-token") token: String,
        @PathVariable notificationId: Long
    ) {
        notificationService.deleteNotificationById(token, notificationId)
    }

    private fun validatePaginationArgs(page: Int, size: Int) {
        require(page >= 0) { "Page should be >= 0. Provided: $page" }
        require(size > 0) { "Size should be > 0. Provided: $size" }
    }
}
