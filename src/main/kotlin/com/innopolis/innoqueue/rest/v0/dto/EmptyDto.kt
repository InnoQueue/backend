package com.innopolis.innoqueue.rest.v0.dto

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Simple DTO for returning string message
 */
data class EmptyDto(
    @JsonProperty("result")
    val result: String
)
