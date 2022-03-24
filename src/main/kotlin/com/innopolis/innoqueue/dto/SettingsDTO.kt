package com.innopolis.innoqueue.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class SettingsDTO(
    @JsonProperty("name")
    val userName: String,
    @JsonProperty("n1")
    val n1: Boolean,
    @JsonProperty("n2")
    val n2: Boolean,
    @JsonProperty("n3")
    val n3: Boolean,
    @JsonProperty("n4")
    val n4: Boolean,
    @JsonProperty("n5")
    val n5: Boolean
)