package com.ohdodok.catchytape.core.domain.repository

import kotlinx.coroutines.flow.Flow

interface AuthRepository {

    fun loginWithGoogle(googleToken: String): Flow<String>

    suspend fun saveToken(token: String)

    suspend fun getToken(): Flow<String>

}