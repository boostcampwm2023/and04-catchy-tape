package com.ohdodok.catchytape.core.domain.usecase.upload

import com.ohdodok.catchytape.core.domain.repository.UuidRepository

import com.ohdodok.catchytape.core.domain.repository.UrlRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.io.File
import javax.inject.Inject

class UploadFileUseCase @Inject constructor(
    private val uuidRepository: UuidRepository,
    private val urlRepository: UrlRepository
) {

    fun getImageUrl(file: File): Flow<Unit> = uuidRepository.getUuid().map { uuid ->
        val preSignedUrl = urlRepository.getImagePreSignedUrl(uuid).first()
        urlRepository.uploadFile(preSignedUrl, file).first()
    }

}