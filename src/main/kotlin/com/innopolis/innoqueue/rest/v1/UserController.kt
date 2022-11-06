package com.innopolis.innoqueue.rest.v1

import com.innopolis.innoqueue.rest.v1.dto.NewUserDTO
import com.innopolis.innoqueue.rest.v1.dto.TokenDTO
import com.innopolis.innoqueue.services.UserService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/user")
@Tag(name = "User")
class UserController(private val service: UserService) {
    @ExceptionHandler(IllegalArgumentException::class)
    fun handleBadRequest(e: IllegalArgumentException): ResponseEntity<String> =
        ResponseEntity(e.message, HttpStatus.BAD_REQUEST)

    @PostMapping("/signup")
    @Operation(
        summary = "Sign Up and get token",
        description = "If you don't have a token yet, you need to register yourself. " +
                "Just send this request to get a new token. Use this token for other requests to identify yourself. " +
                "You won't be able to restore your account if you lost your token.\n" +
                "\n" +
                "- You need to provide `user_name`. This is the nickname for your account. " +
                "It does **not** need to be unique.\n" +
                "- Also provide the Firebase token device for `fcm_token`"
    )
    @ResponseStatus(HttpStatus.OK)
    fun createNewUser(@RequestBody newUserDTO: NewUserDTO): TokenDTO =
        service.createNewUser(newUserDTO.userName, newUserDTO.fcmToken)
}
