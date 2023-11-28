package com.ohdodok.catchytape.core.data.repository

import com.ohdodok.catchytape.core.data.api.StorageApi
import com.ohdodok.catchytape.core.data.api.UploadApi
import com.ohdodok.catchytape.core.domain.repository.UrlRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import javax.inject.Inject

class UrlRepositoryImpl @Inject constructor(
    private val uploadApi: UploadApi,
    private val storageApi: StorageApi
) : UrlRepository {


    override fun getImageUrl(file: File): Flow<String> = flow {
        val urlResponse = uploadApi.postImage(file.toMultipart("image/png"))
        emit(urlResponse.url)
    }

    override fun getAudioUrl(file: File): Flow<String> = flow {
        val urlResponse = uploadApi.postMusic(file.toMultipart("audio/mpeg"))
        emit(urlResponse.url)
    }

    override fun getImagePreSignedUrl(uuid: String): Flow<String> = flow {
        val response = uploadApi.getPreSignedUrl(uuid, "cover")
        emit(response.signedUrl)
    }

    override fun getAudioPreSignedUrl(uuid: String): Flow<String> = flow {
        val response = uploadApi.getPreSignedUrl(uuid, "music")
        emit(response.signedUrl)
    }

    override fun uploadFile(preSignedUrl: String, file: File): Flow<Unit> = flow {
        val response = storageApi.uploadFile(url = preSignedUrl, part = file.toMultipart("image/png"))
        emit(response)
    }

    private fun File.toMultipart(contentType: String): MultipartBody.Part {
        val fileBody = this.asRequestBody(contentType.toMediaTypeOrNull())
        return MultipartBody.Part.createFormData("file", this.name, fileBody)
    }
}