package com.ohdodok.catchytape.core.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MusicResponse (
    @SerialName("music_id")
    val musicId: String,
    val title: String,
    val cover: String,
    @SerialName("music_file")
    val musicFile : String,
    val genre: String,
    val user: NicknameResponse
)