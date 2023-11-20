package com.ohdodok.catchytape.core.domain.signup

import javax.inject.Inject

enum class NicknameValidationResult {
    VALID,
    EMPTY,
    INVALID_LENGTH,
    INVALID_CHARACTER,
}

class NicknameValidationUseCase @Inject constructor() {
    operator fun invoke(nickname: String): NicknameValidationResult {
        val regex = "(^[ㄱ-ㅎ가-힣\\w_.]{2,10}$)".toRegex()

        return when {
            regex.matches(nickname) -> NicknameValidationResult.VALID
            nickname.isBlank() -> NicknameValidationResult.EMPTY
            nickname.length !in 2..10 -> NicknameValidationResult.INVALID_LENGTH
            else -> NicknameValidationResult.INVALID_CHARACTER
        }
    }
}