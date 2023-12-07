package com.ohdodok.catchytape.core.ui.model

data class PlaylistUiModel(
    val id: Int,
    val title: String,
    val thumbnailUrl: String,
    val trackSize: Int,
    val onClick: () -> Unit,
)