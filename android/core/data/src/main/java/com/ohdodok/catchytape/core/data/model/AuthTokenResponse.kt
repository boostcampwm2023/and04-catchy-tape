package com.ohdodok.catchytape.core.data.model

import com.ohdodok.catchytape.core.domain.model.AuthToken
import kotlinx.serialization.Serializable

@Serializable
data class AuthTokenResponse(
    val accessToken: String,
    val refreshToken: String,
) {
    internal fun toDomain(): AuthToken {
        return AuthToken(
            accessToken = accessToken,
            refreshToken = refreshToken
        )
    }
}