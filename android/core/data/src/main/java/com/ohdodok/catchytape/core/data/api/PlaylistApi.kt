package com.ohdodok.catchytape.core.data.api

import com.ohdodok.catchytape.core.data.model.AddMusicToPlaylistRequest
import com.ohdodok.catchytape.core.data.model.MusicResponse
import com.ohdodok.catchytape.core.data.model.PlaylistRequest
import com.ohdodok.catchytape.core.data.model.PlaylistResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface PlaylistApi {

    @GET("playlists")
    suspend fun getPlaylists(): List<PlaylistResponse>

    @POST("playlists")
    suspend fun postPlaylist(
        @Body title: PlaylistRequest
    )

    @GET("playlists/{playlistId}")
    suspend fun getPlaylist(
        @Path("playlistId")playlistId: Int
    ): List<MusicResponse>

    @POST("playlists/{playlistId}")
    suspend fun postMusicToPlaylist(
        @Path("playlistId")playlistId: Int,
        @Body music: AddMusicToPlaylistRequest,
    )
}