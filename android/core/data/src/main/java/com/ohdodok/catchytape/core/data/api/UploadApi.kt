package com.ohdodok.catchytape.core.data.api

import com.ohdodok.catchytape.core.data.model.PreSignedUrlResponse
import com.ohdodok.catchytape.core.data.model.UuidResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface UploadApi {

    @GET("upload/uuid")
    suspend fun getUuid(
    ): UuidResponse

    @GET("upload")
    suspend fun getPreSignedUrl(
        @Query("uuid") uuid: String,
        @Query("type") type: String,
    ): PreSignedUrlResponse

}
