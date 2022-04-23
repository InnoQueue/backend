package com.innopolis.innoqueue.controller.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class NewUserDTO(
    @JsonProperty("user_name")
    val userName: String
)
