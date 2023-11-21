package com.ohdodok.catchytape.core.data.api

import com.ohdodok.catchytape.core.data.model.MusicGenresResponse
import retrofit2.Response
import retrofit2.http.GET

interface MusicApi {

    @GET("musics/genres")
    suspend fun getGenres() : Response<MusicGenresResponse>

}