package com.ohdodok.catchytape.core.domain.usecase

import com.ohdodok.catchytape.core.domain.repository.MusicRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetMusicGenresUseCase @Inject constructor(
    private val musicRepository: MusicRepository
) {

    operator fun invoke(): Flow<List<String>> = musicRepository.getGenres()
}