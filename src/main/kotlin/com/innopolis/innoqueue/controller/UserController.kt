package com.innopolis.innoqueue.controller

import com.innopolis.innoqueue.controller.dto.NewUserDTO
import com.innopolis.innoqueue.controller.dto.TokenDTO
import com.innopolis.innoqueue.service.UserService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/user")
class UserController(private val service: UserService) {
    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.OK)
    fun generateUserToken(@RequestBody newUserDTO: NewUserDTO): TokenDTO =
        service.generateUserToken(newUserDTO.userName)
}