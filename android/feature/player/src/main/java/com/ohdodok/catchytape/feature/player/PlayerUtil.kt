package com.ohdodok.catchytape.feature.player

import android.widget.Button
import androidx.core.net.toUri
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.exoplayer.ExoPlayer
import com.ohdodok.catchytape.core.domain.model.Music

fun ExoPlayer.moveNextMedia() {
    seekToNextMediaItem()
    if (isPlaying) {
        play()
    }
}

fun ExoPlayer.movePreviousMedia() {
    seekToPreviousMediaItem()
    if (isPlaying) {
        play()
    }
}

fun ExoPlayer.changeMoveBtnState(nextBtn: Button, previousBtn: Button) {
    if (nextMediaItemIndex == -1) {
        moveBtnUnable(nextBtn)
    } else {
        moveBtnEnable(nextBtn)
    }

    if (previousMediaItemIndex == -1) {
        moveBtnUnable(previousBtn)
    } else {
        moveBtnEnable(previousBtn)
    }
}

private fun moveBtnUnable(button: Button) {
    button.isEnabled = false
    button.alpha = 0.3f
}

private fun moveBtnEnable(button: Button) {
    button.isEnabled = true
    button.alpha = 1.0f
}


fun getMediasWithMetaData(musics: List<Music>): List<MediaItem> {
    val mediaItemBuilder = MediaItem.Builder()
    val mediaMetadataBuilder = MediaMetadata.Builder()
    return musics.map { music ->
        mediaItemBuilder
            .setMediaId(music.id)
            .setUri(music.musicUrl)
            .setMediaMetadata(
                mediaMetadataBuilder
                    .setArtist(music.artist)
                    .setTitle(music.title)
                    .setArtworkUri(music.imageUrl.toUri())
                    .build()
            )
            .build()
    }
}