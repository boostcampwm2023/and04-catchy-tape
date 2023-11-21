package com.ohdodok.catchytape.core.domain.signup

import com.ohdodok.catchytape.core.domain.repository.AuthRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.single
import javax.inject.Inject

enum class NicknameValidationResult {
    VALID,
    EMPTY,
    INVALID_LENGTH,
    INVALID_CHARACTER,
    DUPLICATED,
}

class ValidateNicknameUseCase @Inject constructor(
    private val authRepository: AuthRepository,
) {
    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    operator fun invoke(nicknameStream: Flow<String>): Flow<NicknameValidationResult> =
        nicknameStream.mapLatest { nickname ->
            val regex = "(^[ㄱ-ㅎ가-힣\\w_.]{2,10}$)".toRegex()

            val result = when {
                regex.matches(nickname) -> {
                    val validNickname = nicknameStream.debounce(300).first()
                    val response = authRepository.isDuplicatedNickname(validNickname).single()

                    if (response) NicknameValidationResult.DUPLICATED
                    else NicknameValidationResult.VALID
                }

                nickname.isBlank() -> NicknameValidationResult.EMPTY
                nickname.length !in 2..10 -> NicknameValidationResult.INVALID_LENGTH
                else -> NicknameValidationResult.INVALID_CHARACTER
            }

            result
        }
}