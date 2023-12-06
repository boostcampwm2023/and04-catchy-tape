package com.ohdodok.catchytape.feature.player

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