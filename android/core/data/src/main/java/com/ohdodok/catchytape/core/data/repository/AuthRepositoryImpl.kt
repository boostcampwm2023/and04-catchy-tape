package com.ohdodok.catchytape.core.data.repository

import com.ohdodok.catchytape.core.data.api.UserApi
import com.ohdodok.catchytape.core.data.model.LoginRequest
import com.ohdodok.catchytape.core.data.model.SignUpRequest
import com.ohdodok.catchytape.core.domain.model.AuthToken
import com.ohdodok.catchytape.core.domain.model.CtErrorType
import com.ohdodok.catchytape.core.domain.model.CtException
import com.ohdodok.catchytape.core.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val userApi: UserApi,
) : AuthRepository {

    override fun loginWithGoogle(googleToken: String): Flow<AuthToken> = flow {
        val loginResponse = userApi.login(LoginRequest(idToken = googleToken))
        emit(loginResponse.toDomain())
    }

    override fun signUpWithGoogle(googleToken: String, nickname: String): Flow<AuthToken> = flow {
        val loginResponse = userApi.signUp(
            SignUpRequest(
                idToken = googleToken,
                nickname = nickname
            )
        )
        emit(loginResponse.toDomain())
    }

    override fun isDuplicatedNickname(nickname: String): Flow<Boolean> = flow {
        val response = userApi.verifyDuplicatedNickname(nickname = nickname)

        when (response.code()) {
            in 200..299 -> emit(false)
            409 -> emit(true)
            else -> throw CtException(response.message(), CtErrorType.SERVER)
        }
    }

    override suspend fun verifyToken(token: String): Boolean {
        return userApi.verify("Bearer ${token}").isSuccessful
    }

    override fun refreshToken() {
        
    }
}