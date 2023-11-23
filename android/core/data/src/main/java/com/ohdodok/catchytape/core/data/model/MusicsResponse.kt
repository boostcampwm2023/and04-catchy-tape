package com.ohdodok.catchytape.core.data.model

import kotlinx.serialization.Serializable

@Serializable
data class MusicsResponse (
    val musics: List<MusicResponse>
)