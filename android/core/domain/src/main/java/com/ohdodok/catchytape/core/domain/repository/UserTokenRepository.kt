package com.ohdodok.catchytape.core.domain.repository

interface UserTokenRepository {

    suspend fun getAccessToken(): String

    suspend fun saveAccessToken(token: String)

    suspend fun getRefreshToken(): String

    suspend fun saveRefreshToken(token: String)
}