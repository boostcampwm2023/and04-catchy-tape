package com.ohdodok.catchytape.core.domain.usecase.login

import com.ohdodok.catchytape.core.domain.repository.AuthRepository
import com.ohdodok.catchytape.core.domain.repository.UserTokenRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val userTokenRepository: UserTokenRepository
) {

    operator fun invoke(googleToken: String): Flow<Unit> =
        authRepository.loginWithGoogle(googleToken).map {
            userTokenRepository.saveAccessToken(it.accessToken)
            userTokenRepository.saveRefreshToken(it.refreshToken)
        }
}