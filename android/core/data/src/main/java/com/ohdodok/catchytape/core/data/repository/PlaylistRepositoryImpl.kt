package com.ohdodok.catchytape.core.data.repository

import com.ohdodok.catchytape.core.data.api.PlaylistApi
import com.ohdodok.catchytape.core.data.api.UserApi
import com.ohdodok.catchytape.core.data.model.AddMusicToPlaylistRequest
import com.ohdodok.catchytape.core.data.model.PlaylistRequest
import com.ohdodok.catchytape.core.data.model.toDomains
import com.ohdodok.catchytape.core.domain.model.Music
import com.ohdodok.catchytape.core.domain.model.Playlist
import com.ohdodok.catchytape.core.domain.repository.PlaylistRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject


class PlaylistRepositoryImpl @Inject constructor(
    private val playlistApi: PlaylistApi,
    private val userApi: UserApi,
) : PlaylistRepository {

    override fun getPlaylists(): Flow<List<Playlist>> = flow {
        val playlistResponse = playlistApi.getPlaylists()
        emit(playlistResponse.map { it.toDomain() })
    }

    override suspend fun postPlaylist(title: String) {
        playlistApi.postPlaylist(PlaylistRequest(title = title))
    }

    override fun getRecentPlaylist(): Flow<List<Music>> = flow {
        val response = userApi.getRecentPlayed()
        emit(response.toDomains())
    }

    override fun getPlaylist(playlistId: Int): Flow<List<Music>> = flow {
        val response = playlistApi.getPlaylist(playlistId)
        emit(response.toDomains())
    }

    override suspend fun addMusicToPlaylist(playlistId: Int, musicId: String) {
        playlistApi.postMusicToPlaylist(
            playlistId = playlistId,
            music = AddMusicToPlaylistRequest(musicId),
        )
    }
}