package com.innopolis.innoqueue.controller

import com.innopolis.innoqueue.controller.dto.TaskDTO
import com.innopolis.innoqueue.dto.ToDoTaskDTO
import com.innopolis.innoqueue.service.ToDoTaskService
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
            "request then such task disappears from this list of to-do tasks.\n" +
            "\n" +
            "It will reappear on the next queue's loop iteration (when all roommates complete or skip this task).\n" +
            "\n" +
            "- `is_important` - `true` if some roommate shook a user to remind about this queue. " +
            "So, this task is urgent.\n" +
            "- `track_expenses` - `true` if such task requires to input expenses"
)
class ToDoTasksController(private val service: ToDoTaskService) {

    @ExceptionHandler(NoSuchElementException::class)
    fun handleNotFound(e: NoSuchElementException): ResponseEntity<String> =
        ResponseEntity(e.message, HttpStatus.NOT_FOUND)

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleBadRequest(e: IllegalArgumentException): ResponseEntity<String> =
        ResponseEntity(e.message, HttpStatus.BAD_REQUEST)

    @GetMapping
    fun getTasks(@RequestHeader("user-token") token: String): List<ToDoTaskDTO> = service.getTasks(token)

    @PostMapping("/done")
    @ResponseStatus(HttpStatus.OK)
    fun completeTask(@RequestHeader("user-token") token: String, @RequestBody task: TaskDTO) =
        service.completeTask(token, task.taskId, task.expenses)

    @PostMapping("/skip")
    @ResponseStatus(HttpStatus.OK)
    fun skipTask(@RequestHeader("user-token") token: String, @RequestBody task: TaskDTO): Unit =
        service.skipTask(token, task.taskId)
}
