package com.ohdodok.catchytape.core.data.repository

import com.ohdodok.catchytape.core.data.api.StorageApi
import com.ohdodok.catchytape.core.domain.repository.StorageRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import javax.inject.Inject

class StorageRepositoryImpl @Inject constructor(
    private val storageApi: StorageApi
) : StorageRepository {

    override fun putImage(preSignedUrl: String, file: File): Flow<Unit> = flow {
        val response =
            storageApi.uploadFile(url = preSignedUrl, part = file.toMultipart("image/png"))
        emit(response)
    }

    override fun putAudio(preSignedUrl: String, file: File): Flow<Unit> = flow {
        val response =
            storageApi.uploadFile(url = preSignedUrl, part = file.toMultipart("audio/mpeg"))
        emit(response)
    }

    private fun File.toMultipart(contentType: String): MultipartBody.Part {
        val fileBody = this.asRequestBody(contentType.toMediaTypeOrNull())
        return MultipartBody.Part.createFormData("file", this.name, fileBody)
    }
}