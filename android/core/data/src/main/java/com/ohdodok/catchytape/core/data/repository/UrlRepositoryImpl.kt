package com.ohdodok.catchytape.core.data.repository

import com.ohdodok.catchytape.core.data.api.UploadApi
import com.ohdodok.catchytape.core.domain.repository.UrlRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import javax.inject.Inject

class UrlRepositoryImpl @Inject constructor(
    private val uploadApi: UploadApi
) : UrlRepository {

    override fun getCoverImageUrl(uuid: String, file: File): Flow<String> = flow {
        val urlResponse = uploadApi.postImage(
            image = file.toImageMultipart(),
            uuid = uuid.toRequestBody(),
            type = "cover".toRequestBody(),
        )
        emit(urlResponse.url)
    }

    override fun getAudioUrl(uuid: String, file: File): Flow<String> = flow {
        val urlResponse =
            uploadApi.postMusic(
                audio = file.toAudioMultipart(),
                uuid = uuid.toRequestBody(),
            )
        emit(urlResponse.url)
    }

    private fun File.toAudioMultipart(): MultipartBody.Part {
        val fileBody = this.asRequestBody("audio/mpeg".toMediaTypeOrNull())
        return MultipartBody.Part.createFormData("file", this.name, fileBody)
    }

    private fun File.toImageMultipart(): MultipartBody.Part {
        val fileBody = this.asRequestBody("image/png".toMediaTypeOrNull())
        return MultipartBody.Part.createFormData("file", this.name, fileBody)
    }

}