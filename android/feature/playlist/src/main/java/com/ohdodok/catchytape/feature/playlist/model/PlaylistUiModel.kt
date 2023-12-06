package com.ohdodok.catchytape.feature.playlist.model

data class PlaylistUiModel(
    val id: Int,
    val title: String,
    val thumbnailUrl: String,
    val trackSize: Int,
    val onClick: () -> Unit,
)