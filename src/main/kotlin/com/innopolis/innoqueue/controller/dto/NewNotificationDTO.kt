package com.innopolis.innoqueue.controller.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class NewNotificationDTO(
    @JsonProperty("any_new")
    val anyNew: Boolean
)
