package com.ohdodok.catchytape.core.data.api

import com.ohdodok.catchytape.core.data.model.PreSignedUrlResponse
import com.ohdodok.catchytape.core.data.model.UrlResponse
import com.ohdodok.catchytape.core.data.model.UuidResponse
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface UploadApi {

    @GET("upload/uuid")
    suspend fun getUuid(
    ): Response<UuidResponse>

    @GET("upload")
    suspend fun getPreSignedUrl(
        @Query("uuid") uuid: String,
        @Query("type") type: String,
    ): Response<PreSignedUrlResponse>

    @Multipart
    @POST("upload/music")
    suspend fun postMusic(
        @Part part: MultipartBody.Part,
    ): Response<UrlResponse>

    @Multipart
    @POST("upload/image")
    suspend fun postImage(
        @Part part: MultipartBody.Part,
    ): Response<UrlResponse>

}
