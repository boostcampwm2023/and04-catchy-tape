package com.ohdodok.catchytape.feature.player

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PlayerState(
    val isPlaying: Boolean = false,
    val currentPositionSecond: Int = 0,
    val duration: Int = 0,
)

sealed interface PlayerEvent {
    data class ShowError(val error: Exception) : PlayerEvent
}

@HiltViewModel
class PlayerViewModel @Inject constructor(

) : ViewModel(), PlayerEventListener {

    private val _uiState = MutableStateFlow(PlayerState())
    val uiState: StateFlow<PlayerState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<PlayerEvent>()
    val events: SharedFlow<PlayerEvent> = _events.asSharedFlow()

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