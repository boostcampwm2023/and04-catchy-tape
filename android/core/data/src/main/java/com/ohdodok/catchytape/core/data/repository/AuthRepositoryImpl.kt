package com.ohdodok.catchytape.core.data.repository

import com.ohdodok.catchytape.core.data.api.UserApi
import com.ohdodok.catchytape.core.data.datasource.TokenLocalDataSource
import com.ohdodok.catchytape.core.data.model.LoginRequest
import com.ohdodok.catchytape.core.data.model.RefreshRequest
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
    private val tokenLocalDataSource: TokenLocalDataSource
) : AuthRepository {

    override fun loginWithGoogle(googleToken: String): Flow<AuthToken> = flow {
        val authTokenResponse = userApi.login(LoginRequest(idToken = googleToken))
        val authToken = authTokenResponse.toDomain()
        saveAuthToken(authToken)
        emit(authToken)
    }

    override fun signUpWithGoogle(googleToken: String, nickname: String): Flow<AuthToken> = flow {
        val authTokenResponse = userApi.signUp(
            SignUpRequest(idToken = googleToken, nickname = nickname)
        )
        val authToken = authTokenResponse.toDomain()
        saveAuthToken(authToken)
        emit(authToken)
    }

    override fun isDuplicatedNickname(nickname: String): Flow<Boolean> = flow {
        val response = userApi.verifyDuplicatedNickname(nickname = nickname)

        when (response.code()) {
            in 200..299 -> emit(false)
            409 -> emit(true)
            else -> throw CtException(response.message(), CtErrorType.SERVER)
        }
    }

    override suspend fun verifyAccessToken(): Boolean {
        val accessToken = tokenLocalDataSource.getAccessToken()
        if (accessToken.isBlank()) return false
        return userApi.verify().isSuccessful
    }

    override fun refreshToken(): Flow<AuthToken> = flow {
        val refreshToken = tokenLocalDataSource.getRefreshToken()
        val authTokenResponse = userApi.refresh(RefreshRequest(refreshToken))

        saveAuthToken(authTokenResponse.toDomain())
    }

    override suspend fun saveAuthToken(authToken: AuthToken) {
        tokenLocalDataSource.saveAccessToken(authToken.accessToken)
        tokenLocalDataSource.saveRefreshToken(authToken.refreshToken)
    }
}