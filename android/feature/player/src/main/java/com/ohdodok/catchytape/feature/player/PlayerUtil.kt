package com.ohdodok.catchytape.feature.player

import android.view.View
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

private fun unableBtn(button: View) {
    button.isEnabled = false
    button.alpha = 0.3f
}

private fun enableBtn(button: View) {
    button.isEnabled = true
    button.alpha = 1.0f
}

fun ExoPlayer.changeMoveBtnState(nextBtn: View, previousBtn: View) {
    if (nextMediaItemIndex == -1) {
        unableBtn(nextBtn)
    } else {
        enableBtn(nextBtn)
    }

    if (previousMediaItemIndex == -1) {
        unableBtn(previousBtn)
    } else {
        enableBtn(previousBtn)
    }
}

fun ExoPlayer.changePlayBtnState(playBtn: View) {
    if (currentMediaItem == null) {
        unableBtn(playBtn)
    } else {
        enableBtn(playBtn)
    }
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