package com.ohdodok.catchytape.core.domain.usecase.player

import com.ohdodok.catchytape.core.domain.model.CurrentPlaylist
import com.ohdodok.catchytape.core.domain.model.Music
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CurrentPlaylistUseCase @Inject constructor() {
    private val _currentPlaylist = Channel<CurrentPlaylist>()
    val currentPlaylist: ReceiveChannel<CurrentPlaylist> = _currentPlaylist

    suspend fun playMusics(startMusic: Music, musics: List<Music>) {
        val newPlaylist = CurrentPlaylist(
            startMusic = startMusic,
            musics = musics,
        )

        _currentPlaylist.send(newPlaylist)
    }

    suspend fun playMusic(music: Music) {
        val newPlaylist = CurrentPlaylist(
            startMusic = music,
            musics = listOf(music),
        )

        _currentPlaylist.send(newPlaylist)
    }
}