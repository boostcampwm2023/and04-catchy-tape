package com.ohdodok.catchytape.core.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.ohdodok.catchytape.core.data.api.UserApi
import com.ohdodok.catchytape.core.data.model.LoginRequest
import com.ohdodok.catchytape.core.data.model.SignUpRequest
import com.ohdodok.catchytape.core.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val userApi: UserApi,
    private val preferenceDataStore: DataStore<Preferences>
) : AuthRepository {

    private val idTokenKey = stringPreferencesKey("idToken")
    private val accessTokenKey = stringPreferencesKey("accessToken")

    override fun loginWithGoogle(googleToken: String): Flow<String> = flow {
        val loginResponse = userApi.login(LoginRequest(idToken = googleToken))
        saveIdToken(googleToken)
        emit(loginResponse.accessToken)
    }

    override fun signUpWithGoogle(googleToken: String, nickname: String): Flow<String> = flow {
        val response = userApi.signUp(SignUpRequest(idToken = googleToken, nickname = nickname))
        if (response.isSuccessful) {
            response.body()?.let { loginResponse ->
                saveIdToken(googleToken)
                emit(loginResponse.accessToken)
            }
        } else {
            // TODO : 네트워크 에러 로직
            throw Exception("회원 가입 실패")
        }
    }


    override suspend fun saveAccessToken(token: String) {
        preferenceDataStore.edit { preferences -> preferences[accessTokenKey] = token }
    }

    override suspend fun saveIdToken(token: String) {
        preferenceDataStore.edit { preferences -> preferences[idTokenKey] = token }
    }

    override suspend fun getIdToken(): String =
        preferenceDataStore.data.map { preferences -> preferences[idTokenKey] ?: "" }.first()

    override fun isDuplicatedNickname(nickname: String): Flow<Boolean> = flow {
        val response = userApi.verifyDuplicatedNickname(nickname = nickname)

        when (response.code()) {
            in 200..299 -> emit(false)
            409 -> emit(true)
            else -> throw RuntimeException("네트워크 에러") // fixme : 예외 처리 로직이 정해지면 수정
        }
    }
}