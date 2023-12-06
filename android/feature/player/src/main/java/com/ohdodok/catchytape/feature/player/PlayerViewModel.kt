package com.ohdodok.catchytape.feature.player

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ohdodok.catchytape.core.domain.model.CtErrorType
import com.ohdodok.catchytape.core.domain.model.CtException
import com.ohdodok.catchytape.core.domain.model.CurrentPlaylist
import com.ohdodok.catchytape.core.domain.model.Music
import com.ohdodok.catchytape.core.domain.repository.MusicRepository
import com.ohdodok.catchytape.core.domain.usecase.player.CurrentPlaylistUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.channels.consumeEach
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
import timber.log.Timber
import javax.inject.Inject

data class PlayerState(
    val currentMusic: Music? = null,
    val isPlaying: Boolean = false,
    val currentPositionSecond: Int = 0,
    val duration: Int = 0,
)

sealed interface PlayerEvent {
    data class ShowError(val error: CtErrorType) : PlayerEvent
}

@HiltViewModel
class PlayerViewModel @Inject constructor(
    private val currentPlaylistUseCase: CurrentPlaylistUseCase,
    private val musicRepository: MusicRepository,
) : ViewModel(), PlayerEventListener {

    private val _currentPlaylist = MutableStateFlow<CurrentPlaylist?>(null)
    val currentPlaylist: StateFlow<CurrentPlaylist?> = _currentPlaylist.asStateFlow()

    private val _uiState = MutableStateFlow(PlayerState())
    val uiState: StateFlow<PlayerState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<PlayerEvent>()
    val events: SharedFlow<PlayerEvent> = _events.asSharedFlow()

    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        val errorType = if (throwable is CtException) throwable.ctError else CtErrorType.UN_KNOWN
        viewModelScope.launch { _events.emit(PlayerEvent.ShowError(errorType)) }
    }

    init {
        observePlaylistChange()
    }

    private fun observePlaylistChange() {
        viewModelScope.launch(exceptionHandler) {
            currentPlaylistUseCase.currentPlaylist.consumeEach {
                _currentPlaylist.value = it
            }
        }
    }

    fun updateCurrentPosition(positionSecond: Int) {
        _uiState.update {
            it.copy(currentPositionSecond = positionSecond)
        }
    }

    fun getPlayedMusicInfo() {
        musicRepository.getPlayedMusicInfo().onEach {
            Timber.d("${it}")
        }.launchIn(viewModelScope)
    }

    fun savePlayedMusicInfo() {
        viewModelScope.launch {
            Timber.d("saveInfo")
            musicRepository.savePlayedMusicInfo("", 0, _uiState.value.currentPositionSecond)
        }
    }

    override fun onPlayingChanged(isPlaying: Boolean) {
        _uiState.update {
            it.copy(isPlaying = isPlaying)
        }
    }

    override fun onMediaItemChanged(index: Int, duration: Int) {
        currentPlaylist.value?.let { playlist ->
            _uiState.update {
                it.copy(duration = duration, currentMusic = playlist.musics[index])
            }
        }
    }
}