package com.ohdodok.catchytape.core.data.api

import com.ohdodok.catchytape.core.data.model.LoginRequest
import com.ohdodok.catchytape.core.data.model.LoginResponse
import com.ohdodok.catchytape.core.data.model.NicknameResponse
import com.ohdodok.catchytape.core.data.model.SignUpRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface UserApi {

    @POST("users/login")
    suspend fun login(
        @Body loginRequest: LoginRequest
    ): LoginResponse

    @POST("users/signup")
    suspend fun signUp(
        @Body signUpRequest: SignUpRequest
    ): Response<LoginResponse>

    @GET("users/duplicate/{nickname}")
    suspend fun verifyDuplicatedNickname(
        @Path("nickname") nickname: String,
    ): Response<NicknameResponse>

    @GET("users/verify")
    suspend fun verify(
        @Header("Authorization") accessToken: String,
    ): Response<Unit>
}