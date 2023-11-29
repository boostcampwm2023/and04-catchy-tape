package com.ohdodok.catchytape.core.data.model


import com.ohdodok.catchytape.core.domain.model.Playlist
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PlaylistResponse(
    @SerialName("playlist_id")
    val playlistId: Int,
    @SerialName("playlist_title")
    val playlistTitle: String,
    @SerialName("music_count")
    val trackSize: Int,
    @SerialName("thumbnail")
    val thumbnailUrl: String?
) {

    internal fun toDomain(): Playlist {
        return Playlist(
            id = playlistId,
            title = playlistTitle,
            thumbnailUrl = thumbnailUrl ?: "",
            trackSize = trackSize
        )
    }
}