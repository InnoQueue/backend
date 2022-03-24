package com.innopolis.innoqueue.controller.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class JoinQueueDTO(
    @JsonProperty("link")
    val link: String
)