package com.ohdodok.catchytape.core.domain.usecase.upload

import com.ohdodok.catchytape.core.domain.repository.StorageRepository
import com.ohdodok.catchytape.core.domain.repository.UuidRepository
import com.ohdodok.catchytape.core.domain.repository.UrlRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import java.io.File
import javax.inject.Inject

class UploadFileUseCase @Inject constructor(
    private val uuidRepository: UuidRepository,
    private val urlRepository: UrlRepository,
    private val storageRepository: StorageRepository
) {

    fun uploadImage(file: File): Flow<String> = uuidRepository.getUuid().map { uuid ->
        val preSignedUrl = urlRepository.getImagePreSignedUrl(uuid).first()
        storageRepository.putImage(preSignedUrl, file).map { preSignedUrl }.first()
    }

    fun uploadAudio(file: File): Flow<String> = uuidRepository.getUuid().map { uuid ->
        val preSignedUrl = urlRepository.getAudioPreSignedUrl(uuid).first()
        storageRepository.putAudio(preSignedUrl, file).map { preSignedUrl }.first()
    }

}