package com.innopolis.innoqueue.dto

import com.fasterxml.jackson.annotation.JsonProperty

class QueueInviteCodeDTO(
    @JsonProperty("pin_code")
    val pinCode: String?,
    @JsonProperty("qr_code")
    val qrCode: String?
)
