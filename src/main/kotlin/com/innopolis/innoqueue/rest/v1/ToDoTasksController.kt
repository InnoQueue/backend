package com.innopolis.innoqueue.rest.v1

import com.innopolis.innoqueue.domain.queue.dto.ToDoTaskDto
import com.innopolis.innoqueue.domain.queue.service.ToDoTaskService
import com.innopolis.innoqueue.rest.v1.dto.SkipTaskDto
import com.innopolis.innoqueue.rest.v1.dto.TaskDto
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

/**
 * Controller with endpoints to work with user to-do tasks
 */
@RestController
@RequestMapping("/tasks")
@Tag(
    name = "To-do tasks",
    description = "List of queues in which it's user's turn to do. " +
            "When a user completes or skips a task by sending `POST /tasks/done` or `POST /tasks/skip` " +
            "request then such task disappears from this list of to-do tasks.\n\n" +
            "It will reappear on the next queue's loop iteration (when all roommates complete or skip this task).\n\n" +
            "- `is_important` - `true` if some roommate shook a user to remind about this queue. " +
            "So, this task is urgent.\n\n" +
            "- `track_expenses` - `true` if such task requires to input expenses.\n"
)
class ToDoTasksController(
    private val service: ToDoTaskService
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
     * GET endpoint for listing user to-do tasks
     * @param token - user token
     */
    @Operation(
        summary = "Get todo-tasks",
        description = "- `is_important` - whether someone shook you (sent reminder). So, this task is urgent now.\n\n" +
                "- `hash_code` - hash code which indicates queues details. " +
                "So, a client will know whether he can request for queue details or use its local cache.\n\n" +
                "- Queues in which there are no participants (only you) won't be shown.\n"
    )
    @GetMapping
    fun getToDoTasks(@RequestHeader("user-token") token: String): List<ToDoTaskDto> = service.getToDoTasks(token)

    /**
     * POST endpoint for completing to-do task
     * @param token - user token
     */
    @Operation(summary = "Complete a to-do task")
    @PostMapping("/done")
    @ResponseStatus(HttpStatus.OK)
    fun completeTask(@RequestHeader("user-token") token: String, @RequestBody task: TaskDto) =
        service.completeTask(token, task.taskId, task.expenses)

    /**
     * POST endpoint for skipping to-do task
     * @param token - user token
     */
    @Operation(summary = "Skip a to-do task")
    @PostMapping("/skip")
    @ResponseStatus(HttpStatus.OK)
    fun skipTask(@RequestHeader("user-token") token: String, @RequestBody task: SkipTaskDto): Unit =
        service.skipTask(token, task.taskId)
}
