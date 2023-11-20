package com.ohdodok.catchytape.core.domain.signup

import com.ohdodok.catchytape.core.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.single
import javax.inject.Inject

enum class NicknameValidationResult {
    VALID,
    EMPTY,
    INVALID_LENGTH,
    INVALID_CHARACTER,
    DUPLICATED,
}

class NicknameValidationUseCase @Inject constructor(
    private val authRepository: AuthRepository,
) {
    operator fun invoke(nickname: String): Flow<NicknameValidationResult> = flow {
        val regex = "(^[ㄱ-ㅎ가-힣\\w_.]{2,10}$)".toRegex()

        val result = when {
            regex.matches(nickname) -> {
                val response = authRepository.isDuplicatedNickname(nickname).single()
                if(response) NicknameValidationResult.DUPLICATED
                else NicknameValidationResult.VALID
            }
            nickname.isBlank() -> NicknameValidationResult.EMPTY
            nickname.length !in 2..10 -> NicknameValidationResult.INVALID_LENGTH
            else -> NicknameValidationResult.INVALID_CHARACTER
        }

        emit(result)
    }
}