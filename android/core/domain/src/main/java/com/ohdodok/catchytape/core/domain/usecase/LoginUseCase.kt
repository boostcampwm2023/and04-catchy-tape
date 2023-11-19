package com.ohdodok.catchytape.core.domain.usecase

import com.ohdodok.catchytape.core.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {

    operator fun invoke(googleToken: String): Flow<Unit> =
        authRepository.loginWithGoogle(googleToken).map { authRepository.saveToken(it) }
}