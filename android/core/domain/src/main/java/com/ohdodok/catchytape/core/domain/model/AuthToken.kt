package com.ohdodok.catchytape.core.domain.model

data class AuthToken(
    val accessToken: String,
    val refreshToken: String
)