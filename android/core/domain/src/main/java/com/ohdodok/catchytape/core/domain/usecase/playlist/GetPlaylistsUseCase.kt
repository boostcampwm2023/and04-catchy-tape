package com.ohdodok.catchytape.core.domain.usecase.playlist

import com.ohdodok.catchytape.core.domain.model.Playlist
import com.ohdodok.catchytape.core.domain.repository.PlaylistRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

const val RECENT_PLAYLIST_ID = 0

class GetPlaylistsUseCase @Inject constructor(
    private val playlistRepository: PlaylistRepository
) {

    operator fun invoke(): Flow<List<Playlist>> = combine(
        playlistRepository.getPlaylists(),
        playlistRepository.getRecentPlaylist()
    ) { playlists, recentPlaylist ->
        (playlists + Playlist(
            id = RECENT_PLAYLIST_ID,
            title = "최근 재생 목록",
            thumbnailUrl = recentPlaylist.firstOrNull()?.imageUrl ?: "",
            trackSize = recentPlaylist.size,
        )).sortedBy { it.id }
    }
}