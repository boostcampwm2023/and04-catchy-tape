package com.ohdodok.catchytape.feature.player

interface PlayerEventListener {

    fun onPlayingChanged(isPlaying: Boolean)

    fun onMediaItemChanged(duration: Int)
}