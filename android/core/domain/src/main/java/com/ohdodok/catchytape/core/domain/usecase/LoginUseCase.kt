package com.ohdodok.catchytape.core.domain.usecase

import com.ohdodok.catchytape.core.domain.repository.AuthRepository
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
}