package com.ohdodok.catchytape.feature.playlist

import androidx.lifecycle.viewModelScope
import com.ohdodok.catchytape.core.domain.model.CtErrorType
import com.ohdodok.catchytape.core.domain.model.Playlist
import com.ohdodok.catchytape.core.domain.repository.PlaylistRepository
import com.ohdodok.catchytape.core.domain.usecase.playlist.GetPlaylistsUseCase
import com.ohdodok.catchytape.core.ui.BaseViewModel
import com.ohdodok.catchytape.core.ui.model.PlaylistUiModel
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

data class PlaylistsUiState(
    val playlists: List<PlaylistUiModel> = emptyList()
)

@HiltViewModel
class PlaylistsViewModel @Inject constructor(
    private val getPlaylistsUseCase: GetPlaylistsUseCase,
    private val playlistRepository: PlaylistRepository
) : BaseViewModel() {
    override suspend fun onError(errorType: CtErrorType) {
        _events.emit(PlaylistsEvent.ShowMessage(errorType))
    }

    private val _events = MutableSharedFlow<PlaylistsEvent>()
    val events = _events.asSharedFlow()

    private val _uiState = MutableStateFlow(PlaylistsUiState())
    val uiState: StateFlow<PlaylistsUiState> = _uiState.asStateFlow()

    fun fetchPlaylists() {
        getPlaylistsUseCase().onEach { playlists ->
            _uiState.update {
                it.copy(
                    playlists = playlists.map { playlist ->
                        PlaylistUiModel(
                            id = playlist.id,
                            title = playlist.title,
                            thumbnailUrl = playlist.thumbnailUrl,
                            trackSize = playlist.trackSize,
                            onClick = { onPlaylistClick(playlist) },
                        )
                    }
                )
            }
        }.launchIn(viewModelScopeWithExceptionHandler)
    }

    fun createPlaylist(playlistTitle: String) {
        viewModelScopeWithExceptionHandler.launch {
            playlistRepository.postPlaylist(playlistTitle)
            fetchPlaylists()
        }
    }

    private fun onPlaylistClick(playlist: Playlist) {
        viewModelScope.launch {
            _events.emit(PlaylistsEvent.NavigateToPlaylistDetail(playlist))
        }
    }
}

sealed interface PlaylistsEvent {

    data class NavigateToPlaylistDetail(val playlist: Playlist) : PlaylistsEvent
    data class ShowMessage(val error: CtErrorType) : PlaylistsEvent
}