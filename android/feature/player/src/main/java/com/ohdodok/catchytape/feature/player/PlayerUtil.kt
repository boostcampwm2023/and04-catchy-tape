package com.ohdodok.catchytape.feature.player

import androidx.core.net.toUri
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.exoplayer.ExoPlayer
import com.ohdodok.catchytape.core.domain.model.Music


private const val POSITION_MOVE_HEAD_STANDARD = 0.05

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

fun ExoPlayer.moveHeadMedia() {
    seekTo(0)
    play()
}

fun ExoPlayer.onPreviousBtnClick() {
    if (currentPosition.toFloat() / duration.toFloat() < POSITION_MOVE_HEAD_STANDARD) {
        movePreviousMedia()
    } else {
        moveHeadMedia()
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