package com.ohdodok.catchytape.core.data.api

import com.ohdodok.catchytape.core.data.model.UrlResponse
import retrofit2.Response
import retrofit2.http.Headers
import retrofit2.http.POST

interface UploadApi {

    @POST("upload/music")
    @Headers("Content-Type: audio/mpeg")
    suspend fun uploadMusic(
    ): Response<UrlResponse>

    @POST("upload/image")
    @Headers("Content-Type: image/png")
    suspend fun uploadImage(
    ): Response<UrlResponse>

}