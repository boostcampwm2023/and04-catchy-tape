package com.ohdodok.catchytape.core.data.datasource

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class TokenLocalDataSource @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {

    private val accessTokenKey = stringPreferencesKey("accessToken")

    suspend fun getAccessToken(): String =
        dataStore.data.map { preferences -> preferences[accessTokenKey] ?: "" }.first()

    suspend fun saveAccessToken(token: String) {
        dataStore.edit { preferences -> preferences[accessTokenKey] = token }
    }
}