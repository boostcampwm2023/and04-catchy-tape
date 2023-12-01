package com.ohdodok.catchytape.core.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.ohdodok.catchytape.core.domain.repository.UserTokenRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class UserTokenRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>,
) : UserTokenRepository {

    private val accessTokenKey = stringPreferencesKey("accessToken")

    override suspend fun getAccessToken(): String =
        dataStore.data.map { preferences -> preferences[accessTokenKey] ?: "" }.first()

    override suspend fun saveAccessToken(token: String) {
        dataStore.edit { preferences -> preferences[accessTokenKey] = token }
    }

}