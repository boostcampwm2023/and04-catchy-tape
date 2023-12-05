package com.ohdodok.catchytape.core.domain.repository

import kotlinx.coroutines.flow.Flow

interface AuthRepository {

    fun loginWithGoogle(googleToken: String): Flow<String>

    fun signUpWithGoogle(googleToken: String, nickname: String): Flow<String>

    fun isDuplicatedNickname(nickname: String): Flow<Boolean>

    suspend fun verifyToken(token: String): Boolean

}