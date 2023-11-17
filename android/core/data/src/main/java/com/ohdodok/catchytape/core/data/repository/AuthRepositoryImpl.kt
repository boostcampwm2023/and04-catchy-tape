package com.ohdodok.catchytape.core.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.ohdodok.catchytape.core.data.api.UserApi
import com.ohdodok.catchytape.core.data.model.LoginRequest
import com.ohdodok.catchytape.core.data.repository.AuthRepositoryImpl.PreferenceKeys.USER_TOKEN
import com.ohdodok.catchytape.core.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val userApi: UserApi,
    private val preferenceDataStore: DataStore<Preferences>
) : AuthRepository {

    override fun loginWithGoogle(googleToken: String): Flow<String> = flow {
        userApi.login(LoginRequest(googleToken)).let { response ->
            if (response.isSuccessful) {
                response.body()?.let { loginResponse ->
                    emit(loginResponse.accessToken)
                }
            } else {
                throw Exception("로그인 실패")
            }
        }
    }

    override suspend fun saveToken(token: String) {
        preferenceDataStore.edit { preferences ->
            preferences[USER_TOKEN] = token
        }
    }

    override suspend fun getToken(): Flow<String> {
        return preferenceDataStore.data.map { preference ->
            preference[USER_TOKEN] ?: ""
        }
    }

    object PreferenceKeys {
        val USER_TOKEN = stringPreferencesKey("token")
    }
}