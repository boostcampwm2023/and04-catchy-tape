package com.ohdodok.catchytape.core.data.repository

import com.ohdodok.catchytape.core.data.api.UserApi
import com.ohdodok.catchytape.core.data.datasource.TokenLocalDataSource
import com.ohdodok.catchytape.core.data.model.LoginRequest
import com.ohdodok.catchytape.core.data.model.SignUpRequest
import com.ohdodok.catchytape.core.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val userApi: UserApi,
    private val tokenDataSource: TokenLocalDataSource,
) : AuthRepository {

    override fun loginWithGoogle(googleToken: String): Flow<String> = flow {
        val response = userApi.login(LoginRequest(idToken = googleToken))
        if (response.isSuccessful) {
            response.body()?.let { loginResponse ->
                emit(loginResponse.accessToken)
            }
        } else if (response.code() == 401) {
            // TODO : 네트워크 에러 로직
            throw Exception("존재하지 않는 유저입니다.")
        }
    }

    override fun signUpWithGoogle(googleToken: String, nickname: String): Flow<String> = flow {
        val response = userApi.signUp(SignUpRequest(idToken = googleToken, nickname = nickname))
        if (response.isSuccessful) {
            response.body()?.let { loginResponse ->
                emit(loginResponse.accessToken)
            }
        } else {
            // TODO : 네트워크 에러 로직
            throw Exception("회원 가입 실패")
        }
    }

    override suspend fun saveAccessToken(token: String) {
        tokenDataSource.saveAccessToken(token)
    }

    override fun isDuplicatedNickname(nickname: String): Flow<Boolean> = flow {
        val response = userApi.verifyDuplicatedNickname(nickname = nickname)

        when (response.code()) {
            in 200..299 -> emit(false)
            409 -> emit(true)
            else -> throw RuntimeException("네트워크 에러") // fixme : 예외 처리 로직이 정해지면 수정
        }
    }

    override suspend fun tryLoginAutomatically(): Boolean {
        val accessToken = tokenDataSource.getAccessToken()

        if (accessToken.isBlank()) return false

        return userApi.verify("Bearer $accessToken").isSuccessful
    }
}