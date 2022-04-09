package com.innopolis.innoqueue.controller.dto

import com.fasterxml.jackson.annotation.JsonProperty

class QueuePinCodeDTO(
    @JsonProperty("pin_code")
    val pinCode: String
)