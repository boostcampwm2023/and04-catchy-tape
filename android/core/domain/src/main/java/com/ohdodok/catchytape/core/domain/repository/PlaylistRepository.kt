package com.ohdodok.catchytape.core.domain.repository

import com.ohdodok.catchytape.core.domain.model.Music
import com.ohdodok.catchytape.core.domain.model.Playlist
import kotlinx.coroutines.flow.Flow

interface PlaylistRepository {

    fun getPlaylists(): Flow<List<Playlist>>

    suspend fun postPlaylist(title: String)

    fun getRecentPlaylist(): Flow<List<Music>>

    fun getPlaylist(playlistId: Int): Flow<List<Music>>

    suspend fun addMusicToPlaylist(playlistId: Int, musicId: String)
}