package com.ohdodok.catchytape.feature.playlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ohdodok.catchytape.core.domain.model.CtErrorType
import com.ohdodok.catchytape.core.domain.model.CtException
import com.ohdodok.catchytape.core.domain.model.Playlist
import com.ohdodok.catchytape.core.domain.repository.PlaylistRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import javax.inject.Inject

data class PlaylistsUiState(
    val playlists: List<Playlist> = emptyList()
)

@HiltViewModel
class PlaylistViewModel @Inject constructor(
    private val playlistRepository: PlaylistRepository
) : ViewModel() {

    private val _events = MutableSharedFlow<PlaylistsEvent>()
    val events = _events.asSharedFlow()

    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        val errorType =
            if (throwable is CtException) throwable.ctError
            else CtErrorType.UN_KNOWN

        viewModelScope.launch { _events.emit(PlaylistsEvent.ShowMessage(errorType)) }
    }

    private val viewModelScopeWithExceptionHandler = viewModelScope + exceptionHandler

    private val _uiState = MutableStateFlow(PlaylistsUiState())
    val uiState: StateFlow<PlaylistsUiState> = _uiState.asStateFlow()


    fun fetchPlaylists() {
        playlistRepository.getPlaylists().onEach { playlists ->
            _uiState.update { it.copy(playlists = playlists) }
        }.launchIn(viewModelScopeWithExceptionHandler)
    }

    fun createPlaylist(playlistTitle: String) {
        viewModelScopeWithExceptionHandler.launch {
            playlistRepository.postPlaylist(playlistTitle)
        }
    }
}


sealed interface PlaylistsEvent {
    data class ShowMessage(val error: CtErrorType) : PlaylistsEvent
}
