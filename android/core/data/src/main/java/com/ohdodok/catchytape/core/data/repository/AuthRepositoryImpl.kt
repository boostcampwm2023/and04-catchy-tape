package com.ohdodok.catchytape.core.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.ohdodok.catchytape.core.data.api.UserApi
import com.ohdodok.catchytape.core.data.model.LoginRequest
import com.ohdodok.catchytape.core.data.model.SignUpRequest
import com.ohdodok.catchytape.core.data.repository.AuthRepositoryImpl.PreferenceKeys.USER_TOKEN
import com.ohdodok.catchytape.core.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val userApi: UserApi,
    private val preferenceDataStore: DataStore<Preferences>
) : AuthRepository {

    override fun loginWithGoogle(googleToken: String): Flow<String> = flow {
        userApi.login(LoginRequest(idToken = googleToken)).let { response ->
            if (response.isSuccessful) {
                response.body()?.let { loginResponse ->
                    emit(loginResponse.accessToken)
                }
            } else if (response.code() == 401) {
                // TODO : 네트워크 에러 로직
                throw Exception("존재하지 않는 유저입니다.")
            }
        }
    }

    override fun signUpWithGoogle(googleToken: String, nickname: String): Flow<String> = flow {
        userApi.signUp(SignUpRequest(idToken = googleToken, nickname = nickname)).let { response ->
            if (response.isSuccessful) {
                response.body()?.let { loginResponse ->
                    emit(loginResponse.accessToken)
                }
            } else {
                // TODO : 네트워크 에러 로직
                throw Exception("회원 가입 실패")
            }
        }
    }

    override suspend fun saveToken(token: String) {
        preferenceDataStore.edit { preferences -> preferences[USER_TOKEN] = token }
    }

    object PreferenceKeys {
        val USER_TOKEN = stringPreferencesKey("token")
    }
}