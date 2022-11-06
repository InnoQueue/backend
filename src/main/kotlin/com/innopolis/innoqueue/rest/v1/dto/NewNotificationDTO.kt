package com.innopolis.innoqueue.rest.v1.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class NewNotificationDTO(
    @JsonProperty("any_new")
    val anyNew: Boolean
)
