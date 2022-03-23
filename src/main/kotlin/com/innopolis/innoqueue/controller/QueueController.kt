package com.innopolis.innoqueue.controller

import com.innopolis.innoqueue.controller.dto.TaskDTO
import com.innopolis.innoqueue.dto.QueuesListDTO
import com.innopolis.innoqueue.dto.ToDoTaskDTO
import com.innopolis.innoqueue.service.QueueService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/queues")
class QueueController(private val service: QueueService) {

    @ExceptionHandler(NoSuchElementException::class)
    fun handleNotFound(e: NoSuchElementException): ResponseEntity<String> =
        ResponseEntity(e.message, HttpStatus.NOT_FOUND)

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleBadRequest(e: IllegalArgumentException): ResponseEntity<String> =
        ResponseEntity(e.message, HttpStatus.BAD_REQUEST)

    @GetMapping
    fun getQueues(@RequestHeader("user-token") token: Long): QueuesListDTO = service.getQueues(token)

//    @PostMapping
//    @ResponseStatus(HttpStatus.OK)
//    fun createQueue(@RequestHeader("user-token") token: Long, @RequestBody task: TaskDTO) =
//        service.createQueue(token, task.taskId, task.expenses)


}