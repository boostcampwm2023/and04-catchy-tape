package com.ohdodok.catchytape.core.domain.usecase

import com.ohdodok.catchytape.core.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {

    operator fun invoke(googleToken: String): Flow<Unit> = flow {
        authRepository.loginWithGoogle(googleToken)
            .catch {
                throw Exception("존재하지 않는 유저입니다.")
            }.collect {
                // 존재하는 유저
                authRepository.saveToken(it)
            }
    }
}