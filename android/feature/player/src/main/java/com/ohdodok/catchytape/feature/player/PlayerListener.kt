package com.ohdodok.catchytape.feature.player

import androidx.media3.common.Player

class PlayerListener(
    private val listener: PlayerEventListener,
) : Player.Listener {

    override fun onEvents(player: Player, events: Player.Events) {
        super.onEvents(player, events)
        when {
            events.contains(Player.EVENT_IS_PLAYING_CHANGED) -> {
                listener.onPlayingChanged(player.isPlaying)
            }

            events.contains(Player.EVENT_TIMELINE_CHANGED) -> {
                val durationMs = player.duration.toInt()
                listener.onMediaItemChanged(durationMs / 1000)
            }
        }
    }
}