package com.ohdodok.catchytape.core.domain.repository

import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun loginWithGoogle(googleToken: String)

    suspend fun saveToken(token: String)

    suspend fun getToken(): Flow<String>

}