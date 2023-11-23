package com.ohdodok.catchytape.core.data.model

import kotlinx.serialization.Serializable

@Serializable
data class MusicRequest (
    val title: String,
    val cover: String,
    val file: String,
    val genre: String
)