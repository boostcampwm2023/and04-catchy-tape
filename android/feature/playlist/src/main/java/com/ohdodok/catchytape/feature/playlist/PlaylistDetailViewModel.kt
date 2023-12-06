package com.ohdodok.catchytape.feature.playlist

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ohdodok.catchytape.core.domain.model.Music
import com.ohdodok.catchytape.core.domain.repository.PlaylistRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import javax.inject.Inject

data class PlaylistDetailUiState(
    val musics: List<Music> = emptyList()
)

@HiltViewModel
class PlaylistDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val playlistRepository: PlaylistRepository,
) : ViewModel() {

    private val playlistId: Int = requireNotNull(savedStateHandle["playlistId"]) {
        "playlistId가 반드시 전달 되어야 해요."
    }

    private val _uiState = MutableStateFlow(PlaylistDetailUiState())
    val uiState: StateFlow<PlaylistDetailUiState> = _uiState.asStateFlow()

    init {
        fetchMusics()
    }

    private fun fetchMusics() {
        playlistRepository.getPlaylist(playlistId)
            .onEach { musics ->
                _uiState.update { it.copy(musics = musics) }
            }
            .launchIn(viewModelScope)
    }
}