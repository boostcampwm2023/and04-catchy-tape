package com.ohdodok.catchytape.core.domain.repository

import kotlinx.coroutines.flow.Flow

interface MusicRepository {

    fun getGenres(): Flow<List<String>>

    fun postMusic(title: String, imageUrl: String, audioUrl: String, genre: String): Flow<Unit>
}