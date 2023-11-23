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
    val isPlaying: Boolean = true
)

sealed interface PlayerEvent {
    data object Play : PlayerEvent
    data object Pause : PlayerEvent
}

@HiltViewModel
class PlayerViewModel @Inject constructor(

) : ViewModel() {

    private val _uiState = MutableStateFlow(PlayerState())
    val uiState: StateFlow<PlayerState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<PlayerEvent>()
    val events: SharedFlow<PlayerEvent> = _events.asSharedFlow()

    private fun sendEvent(event: PlayerEvent) {
        viewModelScope.launch {
            _events.emit(event)
        }
    }

    fun play() {
        sendEvent(PlayerEvent.Play)
        _uiState.update {
            it.copy(isPlaying = true)
        }
    }

    fun pause() {
        sendEvent(PlayerEvent.Pause)
        _uiState.update {
            it.copy(isPlaying = false)
        }
    }
}