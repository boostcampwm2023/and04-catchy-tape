package com.ohdodok.catchytape.core.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.ohdodok.catchytape.core.data.api.UserApi
import com.ohdodok.catchytape.core.data.model.LoginRequest
import com.ohdodok.catchytape.core.data.repository.UserRepositoryImpl.PreferenceKeys.USER_TOKEN
import com.ohdodok.catchytape.core.domain.repository.UserRepository
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val userApi: UserApi,
    private val preferenceDataStore: DataStore<Preferences>
) : UserRepository {

    override suspend fun loginWithGoogle(googleToken: String) {
        userApi.login(LoginRequest(googleToken))
    }

    override suspend fun saveToken(token: String) {
        preferenceDataStore.edit { preferences ->
            preferences[USER_TOKEN] = token
        }
    }

    object PreferenceKeys {
        val USER_TOKEN = stringPreferencesKey("token")
    }
}