package com.ohdodok.catchytape.core.domain.signup

import com.ohdodok.catchytape.core.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class IsDuplicatedNicknameUseCase @Inject constructor(
    private val authRepository: AuthRepository,
) {

    operator fun invoke(nickname: String): Flow<Boolean> =
        authRepository.isDuplicatedNickname(nickname)
}