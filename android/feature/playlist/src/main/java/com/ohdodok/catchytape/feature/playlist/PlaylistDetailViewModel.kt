package com.ohdodok.catchytape.feature.playlist

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ohdodok.catchytape.core.domain.model.CtErrorType
import com.ohdodok.catchytape.core.domain.model.CtException
import com.ohdodok.catchytape.core.domain.model.Music
import com.ohdodok.catchytape.core.domain.usecase.player.CurrentPlaylistUseCase
import com.ohdodok.catchytape.core.domain.usecase.playlist.GetPlaylistUseCase
import com.ohdodok.catchytape.core.ui.MusicAdapter
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

data class PlaylistDetailUiState(
    val musics: List<Music> = emptyList()
)

sealed interface PlaylistDetailEvent {
    data class ShowMessage(val error: CtErrorType) : PlaylistDetailEvent
    data object NavigateToPlayerScreen : PlaylistDetailEvent
}

@HiltViewModel
class PlaylistDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val currentPlaylistUseCase: CurrentPlaylistUseCase,
    private val getPlaylistUseCase: GetPlaylistUseCase,
) : ViewModel(), MusicAdapter.Listener {

    val title: String = requireNotNull(savedStateHandle["title"]) {
        "플레이리스트 제목이 반드시 전달 되어야 해요."
    }
    private val playlistId: Int = requireNotNull(savedStateHandle["playlistId"]) {
        "playlistId가 반드시 전달 되어야 해요."
    }

    private val _uiState = MutableStateFlow(PlaylistDetailUiState())
    val uiState: StateFlow<PlaylistDetailUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<PlaylistDetailEvent>()
    val events: SharedFlow<PlaylistDetailEvent> = _events.asSharedFlow()

    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        val errorType = if (throwable is CtException) throwable.ctError else CtErrorType.UN_KNOWN
        viewModelScope.launch { _events.emit(PlaylistDetailEvent.ShowMessage(errorType)) }
    }

    private val viewModelScopeWithExceptionHandler = viewModelScope + exceptionHandler

    init {
        fetchMusics()
    }

    private fun fetchMusics() {
        getPlaylistUseCase(playlistId).onEach { musics ->
                _uiState.update { it.copy(musics = musics) }
            }.launchIn(viewModelScopeWithExceptionHandler)
    }

    fun playFromFirst() {
        val musics = uiState.value.musics
        if(musics.isEmpty()) return

        play(musics.first())
    }

    private fun play(music: Music) {
        viewModelScopeWithExceptionHandler.launch {
            currentPlaylistUseCase.playMusics(music, uiState.value.musics)
            _events.emit(PlaylistDetailEvent.NavigateToPlayerScreen)
        }
    }

    override fun onClick(music: Music) {
        play(music)
    }
}