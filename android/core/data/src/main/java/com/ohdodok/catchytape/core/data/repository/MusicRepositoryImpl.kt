package com.ohdodok.catchytape.core.data.repository

import com.ohdodok.catchytape.core.data.api.MusicApi
import com.ohdodok.catchytape.core.data.model.MusicRequest
import com.ohdodok.catchytape.core.data.model.MusicResponse
import com.ohdodok.catchytape.core.data.model.toDomains
import com.ohdodok.catchytape.core.domain.model.Music
import com.ohdodok.catchytape.core.domain.repository.MusicRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class MusicRepositoryImpl @Inject constructor(
    private val musicApi: MusicApi
) : MusicRepository {

    override fun getGenres(): Flow<List<String>> = flow {
        val musicGenresResponse = musicApi.getGenres()
        emit(musicGenresResponse.genres)
    }

    override fun getRecentUploadedMusic(): Flow<List<Music>> = flow {
        val musicResponses = musicApi.getRecentUploads()
        emit(musicResponses.toDomains())
    }

    override fun postMusic(
        musicId: String,
        title: String,
        imageUrl: String,
        audioUrl: String,
        genre: String
    ): Flow<Unit> = flow {
        val response = musicApi.postMusic(
            MusicRequest(
                musicId = musicId,
                title = title,
                cover = imageUrl,
                file = audioUrl,
                genre = genre
            )
        )

        emit(response)
    }

    override fun getMyMusics(): Flow<List<Music>> = flow {
        val myMusics = musicApi.getMyUploads()
        emit(myMusics.toDomains())
    }
}
