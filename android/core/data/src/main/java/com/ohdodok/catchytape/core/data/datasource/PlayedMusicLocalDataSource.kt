package com.ohdodok.catchytape.core.data.datasource

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.ohdodok.catchytape.core.domain.model.PlayedMusic
import com.ohdodok.catchytape.core.domain.model.PlayedMusic.Companion.IN_VALID_NUM
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class PlayedMusicLocalDataSource @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    private val musicUrlKey = stringPreferencesKey("musicUrl")
    private val playlistIdKey = intPreferencesKey("playlistId")
    private val playedPositionSecondKey = intPreferencesKey("playedPositionSecondKey")

    suspend fun getPlayedMusicInfo(): PlayedMusic {
        return dataStore.data.map { preferences ->
            PlayedMusic(
                musicUrl = preferences[musicUrlKey] ?: "",
                playlistId = preferences[playlistIdKey] ?: IN_VALID_NUM,
                playedPositionSecond = preferences[playedPositionSecondKey] ?: IN_VALID_NUM
            )
        }.first()
    }

    suspend fun savePlayedMusicInfo(musicUrl: String, playlistId: Int, positionSecond: Int) {
        dataStore.edit { preferences ->
            preferences[musicUrlKey] = musicUrl
            preferences[playlistIdKey] = playlistId
            preferences[playedPositionSecondKey] = positionSecond
        }
    }
}