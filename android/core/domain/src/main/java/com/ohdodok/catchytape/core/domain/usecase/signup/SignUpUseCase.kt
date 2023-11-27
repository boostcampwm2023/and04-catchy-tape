package com.ohdodok.catchytape.core.domain.usecase.signup

import com.ohdodok.catchytape.core.domain.repository.AuthRepository
import com.ohdodok.catchytape.core.domain.repository.UserTokenRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SignUpUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val userTokenRepository: UserTokenRepository
) {

    operator fun invoke(googleToken: String, nickname: String): Flow<Unit> =
        authRepository.signUpWithGoogle(googleToken, nickname).map {
            userTokenRepository.saveAccessToken(it)
        }

}