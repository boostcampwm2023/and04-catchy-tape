package com.ohdodok.catchytape.core.domain.model

data class CurrentPlaylist(
    val startMusic: Music,
    val musics: List<Music>,
) {
    val startMusicIndex: Int

    init {
        require(musics.contains(startMusic)) {
            "재생할 노래가 재생할 노래 목록에 포함되어 있어야 합니다."
        }

        startMusicIndex = musics.indexOf(startMusic)
    }
}