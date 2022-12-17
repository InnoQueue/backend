package com.innopolis.innoqueue.rest.v1.dto

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Simple DTO for returning string message
 */
data class EmptyDTO(
    @JsonProperty("result")
    val result: String
)
