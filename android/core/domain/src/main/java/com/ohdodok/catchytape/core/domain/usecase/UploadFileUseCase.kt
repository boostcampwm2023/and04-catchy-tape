package com.ohdodok.catchytape.core.domain.usecase

import com.ohdodok.catchytape.core.domain.repository.UrlRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.single
import java.io.File
import javax.inject.Inject

class UploadFileUseCase @Inject constructor(
    private val urlRepository: UrlRepository
) {

    fun getImgUrl(file: File): Flow<String> = urlRepository.getUuid().map { uuid ->
        urlRepository.getImageUrl(uuid, file).single()
    }

    fun getAudioUrl(file: File): Flow<String> = urlRepository.getUuid().map { uuid ->
        urlRepository.getAudioUrl(uuid, file).single()
    }
}