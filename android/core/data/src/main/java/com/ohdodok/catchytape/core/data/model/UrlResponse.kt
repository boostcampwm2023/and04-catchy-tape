package com.ohdodok.catchytape.core.data.model

import kotlinx.serialization.Serializable

@Serializable
data class PreSignedUrlResponse(
    val signedUrl: String
)

@Serializable
data class UrlResponse(
    val url: String
)