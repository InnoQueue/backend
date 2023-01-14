package com.innopolis.innoqueue.rest.v1

import com.innopolis.innoqueue.domain.user.dto.TokenDto
import com.innopolis.innoqueue.domain.user.dto.UpdateUserDto
import com.innopolis.innoqueue.domain.user.dto.UserDto
import com.innopolis.innoqueue.domain.user.service.UserService
import com.innopolis.innoqueue.rest.v1.dto.NewUserDto
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

/**
 * Controller with endpoints to work with user model
 */
@RestController
@RequestMapping("/api/v1/user")
@Tag(
    name = "User",
    description = "User profile which you can create and modify its settings."
)
class UserController(private val service: UserService) {

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
     * POST endpoint for creating new user account
     */
    @PostMapping("/signup")
    @Operation(
        summary = "Sign Up to create new user account and get its verification token",
        description = "If you don't have a token yet, you need to register yourself. " +
                "Just send this request to get a new token. Use this token for ALL requests to identify yourself. " +
                "You won't be able to restore your account if you lose your token.\n\n" +
                "- You need to provide non empty `userName`. This is the nickname for your account. " +
                "It does **not** need to be unique. So, you can edit your name anytime you want.\n" +
                "- Also provide the Firebase token device for `fcmToken`. It can't be empty."
    )
    @ResponseStatus(HttpStatus.OK)
    fun createNewUser(@RequestBody newUserDTO: NewUserDto): TokenDto =
        service.createNewUser(newUserDTO.userName, newUserDTO.fcmToken)

    /**
     * GET endpoint for listing user settings
     * @param token - user token
     */
    @Operation(
        summary = "Get user settings",
        description = "Get your current settings\n\n" +
                "- `userName` - the user's name.\n\n" +
                "- other booleans are notification settings, whether a user wishes to receive them.\n" +
                "- `completed` - boolean flag to receive notifications **if someone completes a task**.\n" +
                "- `skipped` - boolean flag to receive notifications **if someone skips a task**.\n" +
                "- `joinedQueue` - boolean flag to receive notifications **if someone joins a queue**.\n" +
                "- `freeze` - boolean flag to receive notifications **if someone freezes or unfreezes a queue**.\n" +
                "- `leftQueue` - boolean flag to receive notifications **if someone leaves a queue**.\n" +
                "- `yourTurn` - boolean flag to receive notifications **who is next responsible " +
                "for a particular task**."
    )
    @GetMapping
    fun getUserSettings(@RequestHeader("user-token") token: String): UserDto = service.getUserSettings(token)

    /**
     * PATCH endpoint for updating user settings
     * @param token - user token
     */
    @Operation(
        summary = "Edit user settings",
        description = "Send a `JSON` body with updated settings.\n\n" +
                "If you want to edit only several fields, then include only them.\n" +
                "Other non provided fields won't be modified.\n\n" +
                "Response returns updated user settings\n\n" +
                "- `userName` - user name. The name can be any non empty string, it's not required to be unique.\n" +
                "- `completed` - boolean flag to receive notifications **if someone completes a task**.\n" +
                "- `skipped` - boolean flag to receive notifications **if someone skips a task**.\n" +
                "- `joinedQueue` - boolean flag to receive notifications **if someone joins a queue**.\n" +
                "- `freeze` - boolean flag to receive notifications **if someone freezes or unfreezes a queue**.\n" +
                "- `leftQueue` - boolean flag to receive notifications **if someone leaves a queue**.\n" +
                "- `yourTurn` - boolean flag to receive notifications **who is next responsible " +
                "for a particular task**."
    )
    @PatchMapping
    @ResponseStatus(HttpStatus.OK)
    fun updateUserSettings(
        @RequestHeader("user-token") token: String,
        @RequestBody settings: UpdateUserDto
    ): UserDto = service.updateUserSettings(token, settings)
}
