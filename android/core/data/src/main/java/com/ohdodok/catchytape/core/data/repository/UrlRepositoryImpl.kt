package com.ohdodok.catchytape.core.data.repository

import com.ohdodok.catchytape.core.data.api.UploadApi
import com.ohdodok.catchytape.core.domain.repository.UrlRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class UrlRepositoryImpl @Inject constructor(
    private val uploadApi: UploadApi,
) : UrlRepository {

    override fun getImagePreSignedUrl(uuid: String): Flow<String> = flow {
        val response = uploadApi.getPreSignedUrl(uuid, "cover")
        emit(response.signedUrl)
    }

    override fun getAudioPreSignedUrl(uuid: String): Flow<String> = flow {
        val response = uploadApi.getPreSignedUrl(uuid, "music")
        emit(response.signedUrl)
    }

}