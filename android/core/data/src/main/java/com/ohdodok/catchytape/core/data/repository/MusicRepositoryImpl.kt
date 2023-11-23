package com.ohdodok.catchytape.core.data.repository

import com.ohdodok.catchytape.core.data.api.MusicApi
import com.ohdodok.catchytape.core.data.model.MusicRequest
import com.ohdodok.catchytape.core.domain.repository.MusicRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class MusicRepositoryImpl @Inject constructor(
    private val musicApi: MusicApi
) : MusicRepository {

    override fun getGenres(): Flow<List<String>> = flow {
        val response = musicApi.getGenres()
        when (response.code()) {
            // TODO : 네트워크 에러 로직 처리
            in 200..299 -> emit(response.body()?.genres ?: emptyList())
            else -> throw RuntimeException("네트워크 에러")
        }
    }

    override fun postMusic(
        title: String,
        imageUrl: String,
        audioUrl: String,
        genre: String
    ): Flow<Unit> = flow {
        val response = musicApi.postMusic(
            MusicRequest(
                title = title,
                cover = imageUrl,
                file = audioUrl,
                genre = genre
            )
        )
        when (response.code()) {
            // TODO : 네트워크 에러 로직 처리
            in 200..299 -> emit(response.body() ?: Unit)
            else -> throw RuntimeException("네트워크 에러")
        }
    }
}