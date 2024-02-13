package com.ohdodok.catchytape.core.domain.repository

import com.ohdodok.catchytape.core.domain.model.AuthToken
import kotlinx.coroutines.flow.Flow

interface AuthRepository {

    fun loginWithGoogle(googleToken: String): Flow<AuthToken>

    fun signUpWithGoogle(googleToken: String, nickname: String): Flow<AuthToken>

    fun isDuplicatedNickname(nickname: String): Flow<Boolean>

    suspend fun verifyAccessToken(): Boolean

    fun refreshToken(): Flow<AuthToken>

    suspend fun saveAuthToken(authToken: AuthToken)
}