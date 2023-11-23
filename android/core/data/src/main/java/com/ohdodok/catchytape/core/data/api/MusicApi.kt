package com.ohdodok.catchytape.core.data.api

import com.ohdodok.catchytape.core.data.model.MusicGenresResponse
import com.ohdodok.catchytape.core.data.model.MusicRequest
import com.ohdodok.catchytape.core.data.model.MusicResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface MusicApi {

    @GET("musics/genres")
    suspend fun getGenres(): Response<MusicGenresResponse>

    @POST("musics")
    suspend fun postMusic(
        @Body music: MusicRequest
    ): Response<Unit>

    @GET("musics/recent-uploads")
    suspend fun getRecentUploads(): Response<List<MusicResponse>>

}