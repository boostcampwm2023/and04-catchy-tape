package com.ohdodok.catchytape.core.domain.model


data class PlayedMusic (
    val musicUrl: String,
    val playlistId: Int,
    val playedPositionSecond: Int,
) {
    companion object {
        const val IN_VALID_NUM: Int = -1
    }
}