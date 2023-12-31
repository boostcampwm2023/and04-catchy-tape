package com.ohdodok.catchytape.core.data.api

import com.ohdodok.catchytape.core.data.model.MusicGenresResponse
import com.ohdodok.catchytape.core.data.model.MusicRequest
import com.ohdodok.catchytape.core.data.model.MusicResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface MusicApi {

    @GET("musics/genres")
    suspend fun getGenres(): MusicGenresResponse

    @POST("musics")
    suspend fun postMusic(
        @Body music: MusicRequest
    )

    @GET("musics/recent-uploads")
    suspend fun getRecentUploads(): List<MusicResponse>

    @GET("musics/my-uploads")
    suspend fun getMyUploads(): List<MusicResponse>


    @GET("musics/search")
    suspend fun getSearchedMusics(
        @Query("keyword") keyword: String
    ): List<MusicResponse>
}