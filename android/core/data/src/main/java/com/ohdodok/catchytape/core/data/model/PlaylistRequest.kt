package com.ohdodok.catchytape.core.data.model


import kotlinx.serialization.Serializable

@Serializable
data class PlaylistRequest(
    val title: String
)