package com.innopolis.innoqueue.controller

import com.innopolis.innoqueue.controller.dto.TaskDTO
import com.innopolis.innoqueue.dto.ToDoTaskDTO
import com.innopolis.innoqueue.service.ToDoTaskService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/tasks")
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