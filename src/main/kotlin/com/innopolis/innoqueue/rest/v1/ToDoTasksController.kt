package com.innopolis.innoqueue.rest.v1

import com.innopolis.innoqueue.rest.v1.dto.TaskDTO
import com.innopolis.innoqueue.dto.ToDoTaskDTO
import com.innopolis.innoqueue.services.ToDoTaskService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

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
            "- `track_expenses` - `true` if such task requires to input expenses\n"
)
class ToDoTasksController(private val service: ToDoTaskService) {

    @ExceptionHandler(NoSuchElementException::class)
    fun handleNotFound(e: NoSuchElementException): ResponseEntity<String> =
        ResponseEntity(e.message, HttpStatus.NOT_FOUND)

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleBadRequest(e: IllegalArgumentException): ResponseEntity<String> =
        ResponseEntity(e.message, HttpStatus.BAD_REQUEST)

    @Operation(
        summary = "Get todo-tasks",
        description = "Queues in which there are no participants (only you) won't be shown.\n" +
                "- `is_important` - whether someone shook you (sent reminder). So, this task is urgent now.\n\n" +
                "- `hash_code` - hash code which indicates queues details.\n" +
                "So, a client will know whether he can request for queue details or use its local cache."
    )
    @GetMapping
    fun getTasks(@RequestHeader("user-token") token: String): List<ToDoTaskDTO> = service.getTasks(token)

    @Operation(summary = "Complete a to-do task")
    @PostMapping("/done")
    @ResponseStatus(HttpStatus.OK)
    fun completeTask(@RequestHeader("user-token") token: String, @RequestBody task: TaskDTO) =
        service.completeTask(token, task.taskId, task.expenses)

    @Operation(summary = "Skip a to-do task")
    @PostMapping("/skip")
    @ResponseStatus(HttpStatus.OK)
    fun skipTask(@RequestHeader("user-token") token: String, @RequestBody task: TaskDTO): Unit =
        service.skipTask(token, task.taskId)
}
