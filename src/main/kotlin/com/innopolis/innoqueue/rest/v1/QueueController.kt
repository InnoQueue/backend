package com.innopolis.innoqueue.rest.v1

import com.innopolis.innoqueue.domain.queue.dto.*
import com.innopolis.innoqueue.domain.queue.service.QueueService
import com.innopolis.innoqueue.domain.queue.service.ToDoTaskService
import com.innopolis.innoqueue.rest.v1.dto.ExpensesDto
import com.innopolis.innoqueue.rest.v1.dto.QueueActivityDto
import com.innopolis.innoqueue.rest.v1.dto.ToDoTasksListDto
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

/**
 * Controller with endpoints to work with queues
 */
@Suppress("TooManyFunctions")
@RestController
@RequestMapping("/api/v1/queues")
@Tag(
    name = "Queue",
    description = "Controller responsible for handling work with Queues. " +
            "A queue is meant to have several people doing the same job in a certain" +
            "sequence. It automatically tracks the order, and works with to-do tasks."
)
class QueueController(
    private val queueService: QueueService,
    private val toDoService: ToDoTaskService
) {

    /**
     * Exception not found handler
     */
    @ExceptionHandler(NoSuchElementException::class)
    fun handleNotFound(e: NoSuchElementException): ResponseEntity<String> =
        ResponseEntity(e.message, HttpStatus.NOT_FOUND)

    /**
     * Exception bad request handler
     */
    @ExceptionHandler(IllegalArgumentException::class)
    fun handleBadRequest(e: IllegalArgumentException): ResponseEntity<String> =
        ResponseEntity(e.message, HttpStatus.BAD_REQUEST)

    /**
     * GET endpoint for listing all queues
     * @param token - user token
     */
    @Operation(
        summary = "Get queues",
        description = "- List all queues which the user joined.\n\n" +
                "- `queueId` - the queue's unique ID\n\n" +
                "- `queueName` - the queue's name to display.\n\n" +
                "- `queueColor` - the queue's label.\n\n" +
                "- `onDutyUserName` - the name of the user who is responsible for this queue.\n\n" +
                "- `active` - shows whether you participate in this queue or not.\n\n" +
                "- `active=true` means that you should contribute to this queue. " +
                "Sometimes this queue will appear in to-dos list\n\n" +
                "- `active=false` means that you temporarily left this queue and don't participate in it. " +
                "This queue won't appear in user's to-do tasks.\n\n" +
                "- To change the queue's `activity` status, check `/queues/{queueId}/activity` endpoint.\n\n"
    )
    @GetMapping
    fun getQueues(@RequestHeader("user-token") token: String, sort: String?): QueuesListDto {
        sort.validateSortParameter()
        return queueService.getQueues(token, sort)
    }

    /**
     * GET endpoint for queue details
     * @param token - user token
     */
    @Operation(
        summary = "Get a queue by id",
        description = "Get the full information about a queue by its id.\n\n" +
                "- `queueId` - the queue's unique ID\n\n" +
                "- `queueName` - the queue's name to display.\n\n" +
                "- `queueColor` - the queue's label.\n\n" +
                "- `trackExpenses` - indicates whether this queue tracks expenses in contribution.\n\n" +
                "- `admin` - indicates whether your are an admin for this queue.\n\n" +
                "- `participants` - users who joined this queue and participate in it. " +
                "They are sorted in terms who is next will be forced to be responsible for a queue.\n\n" +
                "- `participants.active` - if a user participates in this queue or temporarily left it.\n\n" +
                "- `participants.onDuty` - if a user is currentrly responsible for this queue."
    )
    @GetMapping("/{queueId}")
    fun getQueueById(@RequestHeader("user-token") token: String, @PathVariable queueId: Long): QueueDetailsDto =
        queueService.getQueueById(token, queueId)

    /**
     * GET endpoint for creating and returning queue's invite codes
     * @param token - user token
     * @param queueId - id of a queue
     */
    @Operation(
        summary = "Invite to a queue by id",
        description = "Provide a queue's id to get an invitation pin and QR code.\n\n" +
                "- `pinCode` - provide this code in **POST** `/queues/join` request. " +
                "It's length is 6 digits. Pin code will be automatically erased after 60 minutes.\n\n" +
                "- `qrCode` - provide this code in **POST** `/queues/join` request. " +
                "It's length is 48 symbols. QR code will be automatically erased after 24 hours\n\n"
    )
    @PostMapping("/{queueId}/invitation")
    fun getQueueInviteCode(
        @RequestHeader("user-token") token: String,
        @PathVariable queueId: Long
    ): QueueInviteCodeDto =
        queueService.getQueueInviteCode(token, queueId)

    /**
     * POST endpoint for creating new queue
     * @param token - user token
     */
    @Operation(summary = "Create a queue")
    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    fun createQueue(@RequestHeader("user-token") token: String, @RequestBody queue: NewQueueDto): QueueDetailsDto =
        queueService.createQueue(token, queue)

    /**
     * PATCH endpoint for editing existing queue
     * @param token - user token
     */
    @Operation(
        summary = "Edit a queue",
        description = "You can specify only such fields which you want to modify. Other fields will have " +
                "the same value as previously. You can edit a queue only if you are an **admin**.\n\n" +
                "- `participants` - if you want to remove a participant, provide new list with users ids who" +
                "should contribute.\n\n"
    )
    @PatchMapping("/{queueId}")
    @ResponseStatus(HttpStatus.OK)
    fun editQueue(
        @RequestHeader("user-token") token: String,
        @PathVariable queueId: Long,
        @RequestBody queue: EditQueueDto
    ): QueueDetailsDto =
        queueService.editQueue(token, queueId, queue)

    /**
     * POST endpoint for freezing a queue and making it inactive
     * @param token - user token
     */
    @Operation(
        summary = "Change queue's activity",
        description = "- If `active=false`: "
                + "The user temporarily leaves this queue. "
                + "The user won't participate in this queue anymore. So, there won't be any to-do task for it.\n\n"
                + "- If `active=true`: The user can start participate in this queue again."
    )
    @PostMapping("/{queueId}/activity")
    @ResponseStatus(HttpStatus.OK)
    fun changeQueueActivity(
        @RequestHeader("user-token") token: String,
        @PathVariable queueId: Long,
        @RequestBody queueActivityDto: QueueActivityDto
    ) = queueService.freezeUnFreezeQueue(token, queueId, queueActivityDto.active)

    /**
     * DELETE endpoint for deleting or leaving a queue (depends on whether a user is admin of this queue)
     * @param token - user token
     */
    @Operation(
        summary = "Delete a queue",
        description = "- if a user is an admin, this queue will be deleted for everyone.\n\n" +
                "- if a user is **not** an admin, you will just leave this queue. Others will continue using it.\n\n" +
                "- if a user leaves a queue when it was his/her turn, another person is reassigned to this queue."
    )
    @DeleteMapping("/{queueId}")
    @ResponseStatus(HttpStatus.OK)
    fun deleteQueue(@RequestHeader("user-token") token: String, @PathVariable queueId: Long) =
        queueService.deleteQueue(token, queueId)

    /**
     * POST endpoint for joining a queue
     * @param token - user token
     */
    @Operation(
        summary = "Join a queue",
        description = "Provide either `pinCode` or `qrCode` field.\n" +
                "If the code is correct you will receive the queue's description as a result.\n" +
                "No matter whether you joined this queue earlier.\n\n" +
                "In case you provided invalid code, you will get the **Bad Request, 400 response**."
    )
    @PostMapping("/join")
    @ResponseStatus(HttpStatus.OK)
    fun joinQueue(@RequestHeader("user-token") token: String, @RequestBody queue: QueueInviteCodeDto) =
        queueService.joinQueue(token, queue)

    /**
     * POST endpoint for sending notification to a user who is on duty for a particular queue
     * @param token - user token
     */
    @Operation(
        summary = "Shake user",
        description = "Shake the user who is currently responsible for the queue. " +
                "Provide a queue's id to send a shake reminder.\n\n" +
                "The user who is on duty of this queue will receive a reminder notification.\n\n" +
                "**You can't shake yourself!**"
    )
    @PostMapping("/{queueId}/shake")
    @ResponseStatus(HttpStatus.OK)
    fun shakeUser(@RequestHeader("user-token") token: String, @PathVariable queueId: Long) =
        queueService.shakeUser(token, queueId)

    /**
     * GET endpoint for listing user to-do tasks
     * @param token - user token
     */
    @Operation(
        summary = "Get todo-tasks",
        description = "List queues for which your are on duty right now.\n\n" +
                "- `important` - whether someone shook you (sent reminder). So, this task is urgent now.\n\n" +
                "- Queues in which there are no participants (only you) won't be shown.\n"
    )
    @GetMapping("/tasks")
    fun getToDoTasks(@RequestHeader("user-token") token: String): ToDoTasksListDto =
        ToDoTasksListDto(toDoService.getToDoTasks(token))

    /**
     * POST endpoint for completing to-do task
     * @param token - user token
     */
    @Operation(
        summary = "Complete a to-do task.",
        description = "- You should complete a queue for which you are on duty (it's in to-do tasks)\n\n" +
                "- However, you can also add your progress to a queue even if you are not on duty. " +
                "In this case, you'll add +1 contribution to this queue. " +
                "Next time, your turn in to-dos will be skipped." +
                "- `expenses` - provide expenses in pennies if this queue tracks expenses."
    )
    @PostMapping("/{queueId}/complete")
    @ResponseStatus(HttpStatus.OK)
    fun completeTask(
        @RequestHeader("user-token") token: String,
        @PathVariable queueId: Long,
        @RequestBody expensesDto: ExpensesDto?
    ) = toDoService.completeTask(token, queueId, expensesDto?.expenses)

    /**
     * POST endpoint for skipping to-do task
     * @param token - user token
     */
    @Operation(summary = "Skip a to-do task")
    @PostMapping("/{queueId}/skip")
    @ResponseStatus(HttpStatus.OK)
    fun skipTask(
        @RequestHeader("user-token") token: String,
        @PathVariable queueId: Long
    ): Unit = toDoService.skipTask(token, queueId)

    private fun String?.validateSortParameter() {
        if (this != null) {
            when (this) {
                "queue", "participant", "date", "todo" -> return
                else -> throw IllegalArgumentException(
                    "Sort option should be: 'queue', 'participant', 'date' or 'todo'. Provided: $this"
                )
            }
        }
    }
}
