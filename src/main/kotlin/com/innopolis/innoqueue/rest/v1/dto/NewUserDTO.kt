package com.innopolis.innoqueue.rest.v1.dto

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty

data class NewUserDTO(
    @JsonProperty("user_name")
    @JsonIgnore
    val userName: String,
    @JsonProperty("fcm_token")
    @JsonIgnore
    val fcmToken: String
)
