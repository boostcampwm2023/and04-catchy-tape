package com.ohdodok.catchytape.core.data.api

import okhttp3.MultipartBody
import retrofit2.http.Multipart
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Url

interface StorageApi {

    @Multipart
    @PUT
    suspend fun uploadFile(
        @Url url: String,
        @Part part: MultipartBody.Part,
    )
}