package com.innopolis.innoqueue.rest.v1

import com.innopolis.innoqueue.dto.SettingsDTO
import com.innopolis.innoqueue.services.SettingsService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

/**
 * Controller with endpoints to work with user settings
 */
@RestController
@RequestMapping("/settings")
@Tag(
    name = "Settings",
    description = "User settings which you can modify."
)
class SettingsController(private val service: SettingsService) {

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
     * GET endpoint for listing user settings
     * @param token - user token
     */
    @Operation(
        summary = "Get settings",
        description = "Get your current settings\n\n" +
                "- `name` - the user's name.\n\n" +
                "- other booleans are notification settings, whether a user wishes to receive them.\n" +
                "- `completed` - boolean flag to receive notifications **if someone completed a task**.\n" +
                "- `skipped` - boolean flag to receive notifications **if someone skipped a task**.\n" +
                "- `joined_queue` - boolean flag to receive notifications **if someone joined a queue**.\n" +
                "- `freeze` - boolean flag to receive notifications **if someone froze or unfroze a queue**.\n" +
                "- `left_queue` - boolean flag to receive notifications **if someone left a queue**.\n" +
                "- `your_turn` - boolean flag to receive notifications **who is next responsible " +
                "for a particular task**."
    )
    @GetMapping
    fun getSettings(@RequestHeader("user-token") token: String): SettingsDTO = service.getSettings(token)

    /**
     * PATCH endpoint for updating user settings
     * @param token - user token
     */
    @Operation(
        summary = "Edit settings",
        description = "Send a `JSON` body with updated settings.\n\n" +
                "If you want to edit only several fields then include only them.\n" +
                "Other fields will have the old unchanged values.\n\n" +
                "- `name` - user name. The name can be any non empty string, it's not required to be unique.\n" +
                "- `completed` - boolean flag to receive notifications **if someone completed a task**.\n" +
                "- `skipped` - boolean flag to receive notifications **if someone skipped a task**.\n" +
                "- `joined_queue` - boolean flag to receive notifications **if someone joined a queue**.\n" +
                "- `freeze` - boolean flag to receive notifications **if someone froze or unfroze a queue**.\n" +
                "- `left_queue` - boolean flag to receive notifications **if someone left a queue**.\n" +
                "- `your_turn` - boolean flag to receive notifications **who is next responsible " +
                "for a particular task**."
    )
    @PatchMapping
    @ResponseStatus(HttpStatus.OK)
    fun updateSettings(@RequestHeader("user-token") token: String, @RequestBody settings: SettingsDTO): SettingsDTO =
        service.updateSettings(token, settings)
}
