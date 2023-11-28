package com.ohdodok.catchytape.core.data.model


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PlaylistRequest(
    @SerialName("title")
    val title: String
)