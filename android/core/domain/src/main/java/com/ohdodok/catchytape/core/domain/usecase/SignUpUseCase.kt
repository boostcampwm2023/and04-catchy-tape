package com.ohdodok.catchytape.core.domain.usecase

import com.ohdodok.catchytape.core.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SignUpUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {

    operator fun invoke(googleToken: String, nickname: String): Flow<Unit> =
        authRepository.signUpWithGoogle(googleToken, nickname).map { authRepository.saveAccessToken(it) }

}