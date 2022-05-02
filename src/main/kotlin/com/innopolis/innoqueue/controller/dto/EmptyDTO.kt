package com.innopolis.innoqueue.controller.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class EmptyDTO(
    @JsonProperty("result")
    val result: String
)
