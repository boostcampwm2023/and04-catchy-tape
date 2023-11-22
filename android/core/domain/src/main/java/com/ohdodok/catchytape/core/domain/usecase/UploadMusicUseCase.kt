package com.ohdodok.catchytape.core.domain.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class UploadMusicUseCase @Inject constructor() {

    operator fun invoke(imgUrl: String, audioUrl: String, title: String, genre: String): Flow<Unit> = flow {
        // todo : 서버에 업로드
        emit(Unit)
    }
}