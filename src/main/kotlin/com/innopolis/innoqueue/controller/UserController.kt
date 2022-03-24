package com.innopolis.innoqueue.controller

import com.innopolis.innoqueue.controller.dto.TokenDTO
import com.innopolis.innoqueue.service.UserService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/user")
class UserController(private val service: UserService) {
    @GetMapping("/signup")
    fun generateUserToken(): TokenDTO = service.generateUserToken()
}