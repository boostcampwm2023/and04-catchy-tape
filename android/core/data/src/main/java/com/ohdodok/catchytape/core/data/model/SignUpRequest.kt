package com.ohdodok.catchytape.core.data.model

import kotlinx.serialization.Serializable

@Serializable
data class SignUpRequest (
    val nickname: String,
    val idToken: String
)