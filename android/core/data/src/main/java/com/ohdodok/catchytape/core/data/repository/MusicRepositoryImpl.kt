package com.ohdodok.catchytape.core.data.repository

import com.ohdodok.catchytape.core.data.api.MusicApi
import com.ohdodok.catchytape.core.data.model.MusicRequest
import com.ohdodok.catchytape.core.data.model.MusicResponse
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
        emit(musicResponses.map { it.toDomain() })
    }

    override fun postMusic(
        title: String,
        imageUrl: String,
        audioUrl: String,
        genre: String
    ): Flow<Unit> = flow {
        musicApi.postMusic(
            MusicRequest(
                title = title,
                cover = imageUrl,
                file = audioUrl,
                genre = genre
            )
        )
    }
}

fun MusicResponse.toDomain(): Music {
    return Music(
        id = musicId,
        title = title,
        artist = user.nickname,
        imageUrl = cover
    )
}

