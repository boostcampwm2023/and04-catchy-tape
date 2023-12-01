package com.ohdodok.catchytape.core.data.api

import com.ohdodok.catchytape.core.data.model.UrlResponse
import com.ohdodok.catchytape.core.data.model.UuidResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface UploadApi {

    @GET("upload/uuid")
    suspend fun getUuid(
    ): UuidResponse

    @Multipart
    @POST("upload/music")
    suspend fun postMusic(
        @Part audio: MultipartBody.Part,
        @Part("uuid") uuid: RequestBody,
    ): UrlResponse

    @Multipart
    @POST("upload/image")
    suspend fun postImage(
        @Part image: MultipartBody.Part,
        @Part("uuid") uuid: RequestBody,
        @Part("type") type: RequestBody,
    ): UrlResponse

}
