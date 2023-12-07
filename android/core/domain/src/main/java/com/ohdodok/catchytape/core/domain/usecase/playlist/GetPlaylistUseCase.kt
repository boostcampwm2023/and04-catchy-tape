package com.ohdodok.catchytape.core.domain.usecase.playlist

import com.ohdodok.catchytape.core.domain.model.Music
import com.ohdodok.catchytape.core.domain.repository.PlaylistRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetPlaylistUseCase @Inject constructor(
    private val playlistRepository: PlaylistRepository
) {

    operator fun invoke(playlistId: Int): Flow<List<Music>> {
        return if (playlistId == RECENT_PLAYLIST_ID) {
            playlistRepository.getRecentPlaylist()
        } else {
            playlistRepository.getPlaylist(playlistId)
        }
    }

}