package com.innopolis.innoqueue.controller.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class TokenDTO(
    @JsonProperty("token")
    val token: String
)