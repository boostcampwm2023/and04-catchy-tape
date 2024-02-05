package com.ohdodok.catchytape.feature.mypage

import androidx.lifecycle.viewModelScope
import com.ohdodok.catchytape.core.domain.model.CtErrorType
import com.ohdodok.catchytape.core.domain.model.Music
import com.ohdodok.catchytape.core.domain.repository.MusicRepository
import com.ohdodok.catchytape.core.domain.usecase.player.CurrentPlaylistUseCase
import com.ohdodok.catchytape.core.ui.BaseViewModel
import com.ohdodok.catchytape.core.ui.MusicAdapter
import dagger.hilt.android.lifecycle.HiltViewModel
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
import javax.inject.Inject

data class MyMusicsUiState(
    val myMusics: List<Music> = emptyList()
)

sealed interface MyMusicsEvent {
    data class ShowMessage(
        val error: CtErrorType
    ) : MyMusicsEvent

    data object NavigateToPlayerScreen : MyMusicsEvent
}

@HiltViewModel
class MyMusicsViewModel @Inject constructor(
    private val musicRepository: MusicRepository,
    private val currentPlaylistUseCase: CurrentPlaylistUseCase,
) : BaseViewModel(), MusicAdapter.Listener {
    override suspend fun onError(errorType: CtErrorType) {
        _events.emit(MyMusicsEvent.ShowMessage(errorType))
    }

    private val _uiState = MutableStateFlow(MyMusicsUiState())
    val uiState: StateFlow<MyMusicsUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<MyMusicsEvent>()
    val events: SharedFlow<MyMusicsEvent> = _events.asSharedFlow()

    init {
        fetchMyMusics()
    }

    private fun fetchMyMusics() {
        musicRepository.getMyMusics()
            .onEach { musics ->
                _uiState.update {
                    it.copy(myMusics = musics)
                }
            }
            .launchIn(viewModelScopeWithExceptionHandler)
    }

    override fun onClick(music: Music) {
        currentPlaylistUseCase.playMusic(music)
        viewModelScope.launch {
            _events.emit(MyMusicsEvent.NavigateToPlayerScreen)
        }
    }
}