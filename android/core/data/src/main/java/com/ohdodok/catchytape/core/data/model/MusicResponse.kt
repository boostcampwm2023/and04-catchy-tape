package com.ohdodok.catchytape.core.data.model

import kotlinx.serialization.Serializable

@Serializable
data class MusicResponse (
    val musicId: Int,
    val title: String,
    val cover: String,
    val musicFile : String,
    val genre: String,
)