package com.ohdodok.catchytape.core.data.model

import com.ohdodok.catchytape.core.domain.model.Music
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
) {
    fun toDomain(): Music {
        return Music(
            id = musicId,
            title = title,
            artist = user.nickname,
            imageUrl = cover
        )
    }
}

internal fun List<MusicResponse>.toDomains(): List<Music> = this.map { it.toDomain() }