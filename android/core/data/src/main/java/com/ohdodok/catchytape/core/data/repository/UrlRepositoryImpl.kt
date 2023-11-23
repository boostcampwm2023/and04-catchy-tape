package com.ohdodok.catchytape.core.data.repository

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
    private val uploadApi: UploadApi
) : UrlRepository {

    override fun getUuid(): Flow<String> = flow {
        val response = uploadApi.getUuid()
        if (response.isSuccessful) {
            response.body()?.let { uuidResponse -> emit(uuidResponse.uuid) }
        } else {
            // TODO : 네트워크 에러 로직
            throw Exception("uuid 생성 실패")
        }
    }

    override fun getImageUrl(file: File): Flow<String> = flow {
        val response = uploadApi.postImage(file.toMultipart("image/png"))
        if (response.isSuccessful) {
            response.body()?.let { urlResponse -> emit(urlResponse.url) }
        } else {
            // TODO : 네트워크 에러 로직
            throw Exception("이미지 업로드 실패")
        }
    }

    override fun getAudioUrl(file: File): Flow<String> = flow {
        val response = uploadApi.postMusic(file.toMultipart("audio/mpeg"))
        if (response.isSuccessful) {
            response.body()?.let { urlResponse -> emit(urlResponse.url) }
        } else {
            // TODO : 네트워크 에러 로직
            throw Exception("음악 업로드 실패")
        }
    }

    private fun File.toMultipart(contentType: String): MultipartBody.Part {
        val fileBody = this.asRequestBody(contentType.toMediaTypeOrNull())
        return MultipartBody.Part.createFormData("file", this.name, fileBody)
    }
}