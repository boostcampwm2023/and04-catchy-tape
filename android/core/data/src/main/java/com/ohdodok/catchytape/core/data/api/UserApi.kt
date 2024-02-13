package com.ohdodok.catchytape.core.data.api

import com.ohdodok.catchytape.core.data.model.AuthTokenResponse
import com.ohdodok.catchytape.core.data.model.LoginRequest
import com.ohdodok.catchytape.core.data.model.MusicIdRequest
import com.ohdodok.catchytape.core.data.model.MusicResponse
import com.ohdodok.catchytape.core.data.model.NicknameResponse
import com.ohdodok.catchytape.core.data.model.RefreshRequest
import com.ohdodok.catchytape.core.data.model.SignUpRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface UserApi {

    @POST("users/login")
    suspend fun login(
        @Body loginRequest: LoginRequest
    ): AuthTokenResponse

    @POST("users/signup")
    suspend fun signUp(
        @Body signUpRequest: SignUpRequest
    ): AuthTokenResponse

    @POST("users/signup")
    suspend fun refresh(
        @Body refreshRequest: RefreshRequest
    ): AuthTokenResponse

    @GET("users/duplicate/{nickname}")
    suspend fun verifyDuplicatedNickname(
        @Path("nickname") nickname: String,
    ): Response<NicknameResponse>

    @GET("users/verify")
    suspend fun verify(
    ): Response<Unit>

    @GET("users/recent-played")
    suspend fun getRecentPlayed(): List<MusicResponse>

    @PUT("users/recent-played")
    suspend fun putRecentPlayed(
        @Body musicIdRequest: MusicIdRequest,
    )
}