package com.ohdodok.catchytape.feature.player

import androidx.core.net.toUri
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.exoplayer.ExoPlayer
import com.ohdodok.catchytape.core.domain.model.Music


fun ExoPlayer.moveNextMedia() {
    if (isPlaying) {
        seekToNextMediaItem()
        play()
    } else {
        seekToNextMediaItem()
    }
}


fun ExoPlayer.movePreviousMedia() {
    if (isPlaying) {
        seekToPreviousMediaItem()
        play()
    } else {
        seekToPreviousMediaItem()
    }
}


fun getMediasWithMetaData(musics: List<Music>): List<MediaItem> {
    val mediaItemBuilder = MediaItem.Builder()
    val mediaMetadataBuilder = MediaMetadata.Builder()
    return musics.map { music ->
        mediaItemBuilder
            .setMediaId(music.id)
            .setUri("~~~~.m3u8") // TODO :Music domain에서 변수 추가 되면 반영해야 함
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