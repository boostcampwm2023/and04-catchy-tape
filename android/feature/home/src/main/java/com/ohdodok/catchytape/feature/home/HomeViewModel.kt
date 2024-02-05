package com.ohdodok.catchytape.feature.home

import androidx.lifecycle.viewModelScope
import com.ohdodok.catchytape.core.domain.model.CtErrorType
import com.ohdodok.catchytape.core.domain.model.Music
import com.ohdodok.catchytape.core.domain.repository.MusicRepository
import com.ohdodok.catchytape.core.domain.repository.PlaylistRepository
import com.ohdodok.catchytape.core.domain.usecase.player.CurrentPlaylistUseCase
import com.ohdodok.catchytape.core.ui.BaseViewModel
import com.ohdodok.catchytape.core.ui.MusicAdapter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val recentlyUploadedMusics: List<Music> = emptyList(),
    val recentlyPlayedMusics: List<Music> = emptyList(),
) {
    val firstRecentlyPlayedMusicImageUrl: String? = recentlyPlayedMusics.firstOrNull()?.imageUrl
}

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val musicRepository: MusicRepository,
    private val playlistRepository: PlaylistRepository,
    private val currentPlaylistUseCase: CurrentPlaylistUseCase,
) : BaseViewModel(), MusicAdapter.Listener {

    override suspend fun onError(errorType: CtErrorType) {
        _events.emit(HomeEvent.ShowMessage(errorType))
    }

    private val _events = MutableSharedFlow<HomeEvent>()
    val events = _events.asSharedFlow()

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    fun fetchUploadedMusics() {
        musicRepository.getRecentUploadedMusic()
            .onEach { musics ->
                _uiState.update {
                    it.copy(recentlyUploadedMusics = musics)
                }
            }.launchIn(viewModelScopeWithExceptionHandler)
    }

    fun fetchRecentlyPlayedMusics() {
        playlistRepository.getRecentPlaylist()
            .onEach { musics ->
                _uiState.update {
                    it.copy(recentlyPlayedMusics = musics)
                }
            }.launchIn(viewModelScopeWithExceptionHandler)
    }

    fun playRecentlyPlayedMusic() {
        viewModelScope.launch {
            currentPlaylistUseCase.playMusics(
                uiState.value.recentlyPlayedMusics.first(),
                uiState.value.recentlyPlayedMusics
            )
            _events.emit(HomeEvent.NavigateToPlayerScreen)
        }
    }

    override fun onClick(music: Music) {
        viewModelScope.launch {
            currentPlaylistUseCase.playMusics(music, uiState.value.recentlyUploadedMusics)
            _events.emit(HomeEvent.NavigateToPlayerScreen)
        }
    }
}

sealed interface HomeEvent {
    data class ShowMessage(val error: CtErrorType) : HomeEvent
    data object NavigateToPlayerScreen : HomeEvent
}