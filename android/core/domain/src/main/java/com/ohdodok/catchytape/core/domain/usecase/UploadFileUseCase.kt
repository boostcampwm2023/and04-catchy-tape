package com.ohdodok.catchytape.core.domain.usecase

import com.ohdodok.catchytape.core.domain.repository.UrlRepository
import kotlinx.coroutines.flow.Flow
import java.io.File
import javax.inject.Inject

class UploadFileUseCase @Inject constructor(
    private val urlRepository: UrlRepository
) {

    fun getImgUrl(file: File): Flow<String> = urlRepository.getImageUrl(file)

    fun getAudioUrl(file: File): Flow<String> = urlRepository.getAudioUrl(file)


    // TODO : 나중에 쓸 부분임
//    fun getAudioUrl(file: File): Flow<String> = urlRepository.getUuid().map { uuid ->
//        urlRepository.getAudioUrl(uuid, file).single()
//    }

}