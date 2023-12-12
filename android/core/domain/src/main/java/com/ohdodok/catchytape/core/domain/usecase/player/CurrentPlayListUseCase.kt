package com.ohdodok.catchytape.core.domain.usecase.player

import com.ohdodok.catchytape.core.domain.model.CurrentPlaylist
import com.ohdodok.catchytape.core.domain.model.Music
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CurrentPlaylistUseCase @Inject constructor() {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    private val _currentPlaylist = Channel<CurrentPlaylist>()
    val currentPlaylist: ReceiveChannel<CurrentPlaylist> = _currentPlaylist

    fun playMusics(startMusic: Music, musics: List<Music>) {
        val newPlaylist = CurrentPlaylist(
            startMusic = startMusic,
            musics = musics,
        )

        scope.launch { _currentPlaylist.send(newPlaylist) }
    }
}