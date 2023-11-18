package com.ohdodok.catchytape.core.data.api

import com.ohdodok.catchytape.core.data.model.LoginRequest
import com.ohdodok.catchytape.core.data.model.LoginResponse
import com.ohdodok.catchytape.core.data.model.SignUpRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface UserApi {

    @POST("users/login")
    suspend fun login(
        @Body loginRequest: LoginRequest
    ): Response<LoginResponse>

    @POST("users/signup")
    suspend fun signUp(
        @Body signUpRequest: SignUpRequest
    ): Response<LoginResponse>

}