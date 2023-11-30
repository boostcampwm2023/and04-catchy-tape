package com.ohdodok.catchytape.core.data.repository

import com.ohdodok.catchytape.core.data.api.UploadApi
import com.ohdodok.catchytape.core.domain.repository.UploadRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import javax.inject.Inject

class UploadRepositoryImpl @Inject constructor(
    private val uploadApi: UploadApi
) : UploadRepository {

    override fun uploadImage(uuid: String, file: File): Flow<String> = flow {
        val response = uploadApi.postImage(
            image = file.toImageMultipart(),
            uuid = uuid.toRequestBody(),
            type = "cover".toRequestBody(),
        )
        emit(response.url)
    }

    override fun uploadAudio(uuid: String, file: File): Flow<String> = flow {
        val response = uploadApi.postMusic(
            audio = file.toAudioMultipart(),
            uuid = uuid.toRequestBody(),
        )
        emit(response.url)
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