package com.ohdodok.catchytape.core.data.model


import com.ohdodok.catchytape.core.domain.model.Playlist
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PlaylistResponse(
    @SerialName("playlist_Id") // TODO, 서버 변경에 맞춰서 바꿔야 함.
    val playlistId: Int,
    @SerialName("playlist_title")
    val playlistTitle: String
) {

    internal fun toDomain(): Playlist {
        return Playlist(
            id = playlistId,
            title = playlistTitle,
            thumbnailUrl = "",
            trackSize = 0 // TODO, thumbnailUrl, trackSize 는 현재 서버에서 내려주지 않음, 추후 변경 필요
        )
    }
}