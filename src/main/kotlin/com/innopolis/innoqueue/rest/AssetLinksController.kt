package com.innopolis.innoqueue.rest

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * Controller to support Deep links on Android
 */
@RestController
@RequestMapping
@Tag(
    name = "Asset links",
    description = "return JSON for Android deep links"
)
class AssetLinksController(
    @Value("\${assetLinks}")
    private val assetLinks: String? = null
) {
    /**
     * Endpoint for returning assetlinks
     */
    @Suppress("TooGenericExceptionThrown")
    @Operation(
        summary = "get asset links",
        description = "return JSON for Android deep links"
    )
    @GetMapping("/.well-known/assetlinks.json", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun get(): String = if (assetLinks == null || assetLinks == "null") {
        throw RuntimeException("assetlinks.json is not provided")
    } else {
        assetLinks
    }
}
