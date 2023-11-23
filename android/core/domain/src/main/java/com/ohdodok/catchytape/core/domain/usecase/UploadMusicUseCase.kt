package com.ohdodok.catchytape.core.domain.usecase

import com.ohdodok.catchytape.core.domain.repository.MusicRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UploadMusicUseCase @Inject constructor(
    private val musicRepository: MusicRepository
) {

    operator fun invoke(
        imageUrl: String,
        audioUrl: String,
        title: String,
        genre: String
    ): Flow<Unit> = musicRepository.postMusic(
        title = title,
        genre = genre,
        imageUrl = imageUrl,
        audioUrl = audioUrl
    )
}