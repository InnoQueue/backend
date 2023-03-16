package com.innopolis.innoqueue.domain.external.dto

import io.swagger.v3.oas.annotations.media.Schema

/**
 * DTO for showing dev and prod hosts
 */
@Schema(description = "Information about hosts")
data class HostDto(
    @Schema(description = "Dev host which can be used for testing the app")
    val dev: String,

    @Schema(description = "Prod host which represents backend with real users")
    val prod: String
)
