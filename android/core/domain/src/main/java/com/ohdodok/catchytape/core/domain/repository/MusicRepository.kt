package com.ohdodok.catchytape.core.domain.repository

import com.ohdodok.catchytape.core.domain.model.Music
import kotlinx.coroutines.flow.Flow

interface MusicRepository {

    fun getGenres(): Flow<List<String>>

    fun getRecentUploadedMusic(): Flow<List<Music>>

    fun postMusic(musicId: String, title: String, imageUrl: String, audioUrl: String, genre: String): Flow<Unit>

    fun getMyMusics(): Flow<List<Music>>

}