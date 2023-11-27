package com.ohdodok.catchytape.core.domain.usecase.upload

import com.ohdodok.catchytape.core.domain.repository.MusicRepository
import com.ohdodok.catchytape.core.domain.repository.UuidRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class UploadMusicUseCase @Inject constructor(
    private val musicRepository: MusicRepository,
    private val uuidRepository: UuidRepository
) {

    operator fun invoke(
        imageUrl: String, audioUrl: String, title: String, genre: String
    ): Flow<Unit> = uuidRepository.getUuid().map { uuid ->
        musicRepository.postMusic(
            musicId = uuid, title = title, genre = genre, imageUrl = imageUrl, audioUrl = audioUrl
        ).first()
    }
}