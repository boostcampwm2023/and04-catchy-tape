package com.ohdodok.catchytape.core.domain.repository

import com.ohdodok.catchytape.core.domain.model.Music
import com.ohdodok.catchytape.core.domain.model.PlayedMusic
import kotlinx.coroutines.flow.Flow

interface MusicRepository {

    fun getGenres(): Flow<List<String>>

    fun getRecentUploadedMusic(): Flow<List<Music>>

    fun postMusic(musicId: String, title: String, imageUrl: String, audioUrl: String, genre: String): Flow<Unit>

    fun getMyMusics(): Flow<List<Music>>

    fun getSearchedMusics(keyword: String): Flow<List<Music>>

    fun getPlayedMusicInfo(): Flow<PlayedMusic>

    suspend fun savePlayedMusicInfo(musicUrl: String, playlistId: Int, positionSecond: Int)
}