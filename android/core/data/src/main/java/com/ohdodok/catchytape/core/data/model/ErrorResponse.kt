package com.ohdodok.catchytape.core.data.model

import kotlinx.serialization.Serializable


@Serializable
data class ErrorResponse(
    val statusCode: Int,
    val message: String,
    val cTCode: Int = 401 // TODO : 서버가 정해 지면, 변수명 및 기본값 변경 예정
)
