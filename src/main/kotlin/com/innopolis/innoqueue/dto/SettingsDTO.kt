package com.innopolis.innoqueue.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class SettingsDTO(
    @JsonProperty("name")
    val userName: String?,
    @JsonProperty("completed")
    val completed: Boolean?,
    @JsonProperty("skipped")
    val skipped: Boolean?,
    @JsonProperty("joined_queue")
    val joinedQueue: Boolean?,
    @JsonProperty("freeze")
    val freeze: Boolean?,
    @JsonProperty("left_queue")
    val leftQueue: Boolean?,
    @JsonProperty("your_turn")
    val yourTurn: Boolean?
)
