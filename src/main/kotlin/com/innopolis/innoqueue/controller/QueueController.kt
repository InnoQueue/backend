package com.innopolis.innoqueue.controller

import com.innopolis.innoqueue.controller.dto.JoinQueueDTO
import com.innopolis.innoqueue.dto.EditQueueDTO
import com.innopolis.innoqueue.dto.NewQueueDTO
import com.innopolis.innoqueue.dto.QueueDTO
import com.innopolis.innoqueue.dto.QueuesListDTO
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
    fun getQueues(@RequestHeader("user-token") token: String): QueuesListDTO = service.getQueues(token)

    @GetMapping("/{queueId}")
    fun getQueues(@RequestHeader("user-token") token: String, @PathVariable queueId: Long): QueueDTO =
        service.getQueueById(token, queueId)

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    fun createQueue(@RequestHeader("user-token") token: String, @RequestBody queue: NewQueueDTO): QueueDTO =
        service.createQueue(token, queue)

    @PatchMapping
    @ResponseStatus(HttpStatus.OK)
    fun editQueue(@RequestHeader("user-token") token: String, @RequestBody queue: EditQueueDTO): QueueDTO =
        service.editQueue(token, queue)

    @PostMapping("/freeze/{queueId}")
    @ResponseStatus(HttpStatus.OK)
    fun freezeQueue(@RequestHeader("user-token") token: String, @PathVariable queueId: Long) =
        service.freezeUnFreezeQueue(token, queueId, false)

    @PostMapping("/unfreeze/{queueId}")
    @ResponseStatus(HttpStatus.OK)
    fun unfreezeQueue(@RequestHeader("user-token") token: String, @PathVariable queueId: Long) =
        service.freezeUnFreezeQueue(token, queueId, true)

    @DeleteMapping("/{queueId}")
    @ResponseStatus(HttpStatus.OK)
    fun deleteQueue(@RequestHeader("user-token") token: String, @PathVariable queueId: Long) =
        service.deleteQueue(token, queueId)

    @PostMapping("/join")
    @ResponseStatus(HttpStatus.OK)
    fun joinQueue(@RequestHeader("user-token") token: String, @RequestBody queue: JoinQueueDTO) =
        service.joinQueue(token, queue)

    @PostMapping("/shake/{queueId}")
    @ResponseStatus(HttpStatus.OK)
    fun shakeUser(@RequestHeader("user-token") token: String, @PathVariable queueId: Long) =
        service.shakeUser(token, queueId)
}