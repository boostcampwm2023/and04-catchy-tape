package com.ohdodok.catchytape.core.domain.usecase

import com.ohdodok.catchytape.core.domain.repository.AuthRepository
import javax.inject.Inject

class AutomaticallyLoginUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(): Boolean = authRepository.tryLoginAutomatically()
}