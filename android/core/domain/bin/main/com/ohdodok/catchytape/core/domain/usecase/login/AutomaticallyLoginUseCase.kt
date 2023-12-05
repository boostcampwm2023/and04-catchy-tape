package com.ohdodok.catchytape.core.domain.usecase.login

import com.ohdodok.catchytape.core.domain.repository.AuthRepository
import com.ohdodok.catchytape.core.domain.repository.UserTokenRepository
import javax.inject.Inject

class AutomaticallyLoginUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val userTokenRepository: UserTokenRepository
) {

    suspend operator fun invoke(): Boolean {
        val accessToken = userTokenRepository.getAccessToken()
        if (accessToken.isBlank()) return false
        return authRepository.verifyToken(accessToken)
    }
}