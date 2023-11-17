package com.ohdodok.catchytape.core.data.model

import kotlinx.serialization.Serializable

@Serializable
data class LoginResponse (
    val accessToken: String
)