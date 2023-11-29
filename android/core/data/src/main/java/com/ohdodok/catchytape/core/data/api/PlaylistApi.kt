package com.ohdodok.catchytape.core.data.api

import com.ohdodok.catchytape.core.data.model.PlaylistRequest
import com.ohdodok.catchytape.core.data.model.PlaylistResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface PlaylistApi {

    @GET("playlists")
    suspend fun getPlaylists(): List<PlaylistResponse>

    @POST("playlists")
    suspend fun postPlaylist(
        @Body title: PlaylistRequest
    )

}