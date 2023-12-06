package com.ohdodok.catchytape.feature.playlist

import androidx.lifecycle.ViewModel
import com.ohdodok.catchytape.core.domain.model.Music
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

data class PlaylistDetailUiState(
    val musics: List<Music> = listOf(
        Music(
            id = "1",
            title = "title",
            artist = "artist",
            imageUrl = "https://i.pinimg.com/736x/6a/be/29/6abe2929274d6459c815ac752fb0c057.jpg",
            musicUrl = "",
        )
    )
)

@HiltViewModel
class PlaylistDetailViewModel @Inject constructor(

) : ViewModel() {

    private val _uiState = MutableStateFlow(PlaylistDetailUiState())
    val uiState: StateFlow<PlaylistDetailUiState> = _uiState.asStateFlow()
}