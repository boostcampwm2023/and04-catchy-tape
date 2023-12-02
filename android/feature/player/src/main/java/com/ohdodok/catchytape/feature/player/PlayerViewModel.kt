package com.ohdodok.catchytape.feature.player

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ohdodok.catchytape.core.domain.model.CtErrorType
import com.ohdodok.catchytape.core.domain.model.CtException
import com.ohdodok.catchytape.core.domain.model.Music
import com.ohdodok.catchytape.core.domain.repository.PlaylistRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import javax.inject.Inject

data class PlayerState(
    val playlist: List<Music> = emptyList(),
    val isPlaying: Boolean = false,
    val currentPositionSecond: Int = 0,
    val duration: Int = 0,
)

sealed interface PlayerEvent {
    data class ShowError(val error: CtErrorType) : PlayerEvent
}

@HiltViewModel
class PlayerViewModel @Inject constructor(
    private val playlistRepository: PlaylistRepository,
) : ViewModel(), PlayerEventListener {

    private val _uiState = MutableStateFlow(PlayerState())
    val uiState: StateFlow<PlayerState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<PlayerEvent>()
    val events: SharedFlow<PlayerEvent> = _events.asSharedFlow()

    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        val errorType = if (throwable is CtException) throwable.ctError else CtErrorType.UN_KNOWN
        viewModelScope.launch { _events.emit(PlayerEvent.ShowError(errorType)) }
    }

    private val viewModelScopeWithExceptionHandler = viewModelScope + exceptionHandler

    init {
        fetchRecentPlaylist()
    }

    private fun fetchRecentPlaylist() {
        playlistRepository.getRecentPlaylist().onEach { recentPlaylist ->
            _uiState.update { it.copy(playlist = recentPlaylist) }
        }.launchIn(viewModelScopeWithExceptionHandler)
    }

    private fun sendEvent(event: PlayerEvent) {
        viewModelScope.launch {
            _events.emit(event)
        }
    }

    fun updateCurrentPosition(positionSecond: Int) {
        _uiState.update {
            it.copy(currentPositionSecond = positionSecond)
        }
    }

    override fun onPlayingChanged(isPlaying: Boolean) {
        _uiState.update {
            it.copy(isPlaying = isPlaying)
        }
    }

    override fun onMediaItemChanged(duration: Int) {
        _uiState.update { it.copy(duration = duration) }
    }
}