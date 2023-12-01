package com.ohdodok.catchytape.core.data.model

import kotlinx.serialization.Serializable


@Serializable
data class ErrorResponse(
    val message: String,
    val errorCode: Int = 0,
    val statusCode: Int = 0
)
