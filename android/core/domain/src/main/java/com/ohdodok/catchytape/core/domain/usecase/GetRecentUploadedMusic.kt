package com.ohdodok.catchytape.core.domain.usecase

import com.ohdodok.catchytape.core.domain.model.Music
import com.ohdodok.catchytape.core.domain.repository.MusicRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetRecentUploadedMusic @Inject constructor(
    private val musicRepository: MusicRepository
) {
    operator fun invoke(): Flow<List<Music>> = musicRepository.getRecentUploadedMusic()
}