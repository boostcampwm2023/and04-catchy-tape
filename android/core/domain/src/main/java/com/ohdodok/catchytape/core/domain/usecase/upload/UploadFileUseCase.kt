package com.ohdodok.catchytape.core.domain.usecase.upload

import com.ohdodok.catchytape.core.domain.repository.UploadRepository
import com.ohdodok.catchytape.core.domain.repository.UuidRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.io.File
import javax.inject.Inject

class UploadFileUseCase @Inject constructor(
    private val uuidRepository: UuidRepository,
    private val uploadRepository: UploadRepository,
) {

    fun uploadAudio(audioFile: File): Flow<String> = uuidRepository.getUuid().map { uuid ->
        uploadRepository.uploadAudio(uuid, audioFile).first()
    }

    fun uploadMusicCover(imageFile: File): Flow<String> = uuidRepository.getUuid().map { uuid ->
        uploadRepository.uploadImage(uuid, imageFile).first()
    }
}