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
        return NicknameValidationResult.EMPTY
//        TODO("닉네임 유효성 검사 구현")
    }
}