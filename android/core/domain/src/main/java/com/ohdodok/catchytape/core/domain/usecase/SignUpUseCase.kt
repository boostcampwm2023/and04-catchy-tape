package com.ohdodok.catchytape.core.domain.usecase

import com.ohdodok.catchytape.core.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class SignUpUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {

    operator fun invoke(googleToken: String, nickname: String): Flow<Unit> = flow {
        authRepository.signUpWithGoogle(googleToken, nickname).collect {
                authRepository.saveToken(it)
                emit(Unit)
            }
    }
}