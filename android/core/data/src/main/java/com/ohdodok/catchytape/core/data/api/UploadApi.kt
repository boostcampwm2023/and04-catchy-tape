package com.ohdodok.catchytape.core.data.api

import com.ohdodok.catchytape.core.data.model.UrlResponse
import com.ohdodok.catchytape.core.data.model.UuidResponse
import kotlinx.serialization.SerialName
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.Part
import retrofit2.http.Query

interface UploadApi {

    @GET("upload/uuid")
    suspend fun getUuid(
    ): Response<UuidResponse>

    @GET("upload")
    @Multipart
    suspend fun getUrl(
        @Part file: MultipartBody.Part,
        @Query("uuid") uuid: String,
        @Query("type") type: FileType,
    ): Response<UrlResponse>

}

enum class FileType {
    @SerialName("music")
    MUSIC,
    @SerialName("cover")
    COVER
}