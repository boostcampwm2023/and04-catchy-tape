package com.ohdodok.catchytape.core.domain.repository

import kotlinx.coroutines.flow.Flow

interface MusicRepository {

    fun getGenres(): Flow<List<String>>
}