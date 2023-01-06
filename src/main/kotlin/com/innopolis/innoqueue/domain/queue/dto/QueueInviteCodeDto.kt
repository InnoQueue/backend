package com.innopolis.innoqueue.domain.queue.dto

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * DTO for returning queue invite credentials
 */
class QueueInviteCodeDto(
    @JsonProperty("pin_code")
    val pinCode: String?,
    @JsonProperty("qr_code")
    val qrCode: String?
)
