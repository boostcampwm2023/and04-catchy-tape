package com.ohdodok.catchytape.core.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MusicRequest (
    @SerialName("music_id")
    val musicId: String,
    val title: String,
    val cover: String,
    val file: String,
    val genre: String
)