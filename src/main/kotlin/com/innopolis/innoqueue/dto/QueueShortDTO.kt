package com.innopolis.innoqueue.dto

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * DTO for returning a short queue description
 */
class QueueShortDTO(
    @JsonProperty("id")
    val queueId: Long,
    @JsonProperty("name")
    val queueName: String,
    @JsonProperty("color")
    val queueColor: String,
    @JsonProperty("hash_code")
    val hashCode: Int
)
