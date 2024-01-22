package com.ohdodok.catchytape.core.domain.usecase.upload

import com.ohdodok.catchytape.core.domain.repository.UploadRepository
import kotlinx.coroutines.flow.Flow
import java.io.File
import javax.inject.Inject

class UploadFileUseCase @Inject constructor(
    private val uploadRepository: UploadRepository,
) {
    fun uploadAudio(audioFile: File, uuid: String): Flow<String> {
        return uploadRepository.uploadAudio(uuid, audioFile)
    }

    fun uploadMusicCover(imageFile: File, uuid: String): Flow<String> {
        return uploadRepository.uploadImage(uuid, imageFile)
    }
}