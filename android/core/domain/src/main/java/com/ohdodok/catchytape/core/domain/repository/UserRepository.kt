package com.ohdodok.catchytape.core.domain.repository

interface UserRepository {
    suspend fun loginWithGoogle(googleToken: String)
}