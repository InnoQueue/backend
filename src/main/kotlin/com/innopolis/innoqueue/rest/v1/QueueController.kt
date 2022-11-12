package com.innopolis.innoqueue.rest.v1

import com.innopolis.innoqueue.dto.*
import com.innopolis.innoqueue.services.QueueService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@Suppress("TooManyFunctions")
@RestController
@RequestMapping("/queues")
@Tag(
    name = "Queue",
    description = "Controller responsible for handling work with Queues. " +
            "A queue is meant to have several people doing the same job in a certain" +
            "sequence. It automatically tracks the order, and works with to-do tasks."
)
class QueueController(private val service: QueueService) {

    @ExceptionHandler(NoSuchElementException::class)
    fun handleNotFound(e: NoSuchElementException): ResponseEntity<String> =
        ResponseEntity(e.message, HttpStatus.NOT_FOUND)

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleBadRequest(e: IllegalArgumentException): ResponseEntity<String> =
        ResponseEntity(e.message, HttpStatus.BAD_REQUEST)

    @Operation(
        summary = "Get queues",
        description = "- List all queues which the user joined.\n\n" +
                "- `activeQueues` - queues in which a user actively participates. " +
                "The user should contribute to these queues.\n\n" +
                "- `frozenQueues` - queues which a user temporarily left. " +
                "These queues won't appear in user's To-do tasks since he/she does not participate in them.\n\n" +
                "- To change the queue's status, check `/queues/freeze` and `/queues/unfreeze` endpoints.\n\n" +
                "- `queueId` - the queue's unique ID\n\n" +
                "- `queueName` - the queue's name to display.\n\n" +
                "- `queueColor` - the queue's label.\n\n" +
                "- `hash_code` - hash code which indicates queue details. " +
                "So, a client will know whether he can request for queue details or use its local cache."
    )
    @GetMapping
    fun getQueues(@RequestHeader("user-token") token: String): QueuesListDTO = service.getQueues(token)

    @Operation(
        summary = "Get a queue by id",
        description = "Get the full information about a queue by its id.\n\n" +
                "- `on_duty` - the user who is responsible for this queue now.\n\n" +
                "- `is_on_duty` - shows whether it is your turn of this queue.\n\n" +
                "- `participants` - users who joined this queue and participate in it. " +
                "They are sorted in terms who is next will be forced to be responsible for a queue."
    )
    @GetMapping("/{queueId}")
    fun getQueueById(@RequestHeader("user-token") token: String, @PathVariable queueId: Long): QueueDTO =
        service.getQueueById(token, queueId)

    @Operation(
        summary = "Invite to a queue by id",
        description = "Provide a queue's id to get an invitation pin and QR code.\n\n" +
                "- `pin_code` - use this code in **POST** `/queues/join` request.\n\n" +
                "- `qr_code` - use this code in **POST** `/queues/join` request.\n\n" +
                "- Pin code will be automatically destroyed after 60 mins and it's length is 6 digits.\n\n" +
                "- QR code will be automatically destroyed after 24 hours and it's length is 48 symbols."
    )
    @GetMapping("/invite/{queueId}")
    fun getQueueInviteCode(
        @RequestHeader("user-token") token: String,
        @PathVariable queueId: Long
    ): QueueInviteCodeDTO =
        service.getQueueInviteCode(token, queueId)

    @Operation(summary = "Create a queue")
    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    fun createQueue(@RequestHeader("user-token") token: String, @RequestBody queue: NewQueueDTO): QueueDTO =
        service.createQueue(token, queue)

    @Operation(
        summary = "Edit a queue",
        description = "You can specify only such fields which you want to modify. Other fields will have " +
                "the same value as previously.\n\n" +
                "The only required field is `id`."
    )
    @PatchMapping
    @ResponseStatus(HttpStatus.OK)
    fun editQueue(@RequestHeader("user-token") token: String, @RequestBody queue: EditQueueDTO): QueueDTO =
        service.editQueue(token, queue)

    @Operation(
        summary = "Freeze a queue",
        description = "- The user temporarily leaves this queue.\n\n"
                + "- The user won't participate in this queue anymore. So, there won't be any to-do task for it.\n\n"
                + "- The user can start participate in this queue again after calling `/queues/unfreeze` endpoint.\n\n"
    )
    @PostMapping("/freeze/{queueId}")
    @ResponseStatus(HttpStatus.OK)
    fun freezeQueue(@RequestHeader("user-token") token: String, @PathVariable queueId: Long) =
        service.freezeUnFreezeQueue(token, queueId, false)

    @Operation(
        summary = "Unfreeze a queue",
        description = "Call this endpoint to resume the user's participation in this queue and get to-do task for it."
    )
    @PostMapping("/unfreeze/{queueId}")
    @ResponseStatus(HttpStatus.OK)
    fun unfreezeQueue(@RequestHeader("user-token") token: String, @PathVariable queueId: Long) =
        service.freezeUnFreezeQueue(token, queueId, true)

    @Operation(
        summary = "Delete a queue",
        description = "- if a user is an admin, this queue will be deleted for everyone.\n\n" +
                "- if a user is **not** an admin, you will just leave this queue. Others will continue using it.\n\n" +
                "- if a user leaves a queue when it was his/her turn, another person is reassigned to this queue."
    )
    @DeleteMapping("/{queueId}")
    @ResponseStatus(HttpStatus.OK)
    fun deleteQueue(@RequestHeader("user-token") token: String, @PathVariable queueId: Long) =
        service.deleteQueue(token, queueId)

    @Operation(
        summary = "Join a queue",
        description = "Use either `pin_code` or `qr_code` field.\n" +
                "If the code is correct you will get the queue description as a result.\n" +
                "No matter whether you joined this queue earlier.\n\n" +
                "In case you provided invalid code, you will get the **Bad Request, 400 response**."
    )
    @PostMapping("/join")
    @ResponseStatus(HttpStatus.OK)
    fun joinQueue(@RequestHeader("user-token") token: String, @RequestBody queue: QueueInviteCodeDTO) =
        service.joinQueue(token, queue)

    @Operation(
        summary = "Shake user",
        description = "Shake the user who is currently responsible in the queue." +
                "Provide a queue's id to send a shake reminder.\n" +
                "The user who is on duty of this queue will receive a reminder notification.\n\n" +
                "`You can't shake yourself!`"
    )
    @PostMapping("/shake/{queueId}")
    @ResponseStatus(HttpStatus.OK)
    fun shakeUser(@RequestHeader("user-token") token: String, @PathVariable queueId: Long) =
        service.shakeUser(token, queueId)
}
