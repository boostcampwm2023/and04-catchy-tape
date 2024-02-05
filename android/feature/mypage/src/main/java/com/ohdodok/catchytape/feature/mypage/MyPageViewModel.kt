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

data class MyPageUiState(
    val myMusics: List<Music> = emptyList()
)

@HiltViewModel
class MyPageViewModel @Inject constructor(
    private val musicRepository: MusicRepository,
    private val currentPlaylistUseCase: CurrentPlaylistUseCase,
) : BaseViewModel(), MusicAdapter.Listener {
    override suspend fun onError(errorType: CtErrorType) {
        _events.emit(MyPageEvent.ShowMessage(errorType))
    }

    private val _uiState = MutableStateFlow(MyPageUiState())
    val uiState: StateFlow<MyPageUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<MyPageEvent>()
    val events: SharedFlow<MyPageEvent> = _events.asSharedFlow()

    fun fetchMyMusics(count: Int) {
        musicRepository.getMyMusics()
            .onEach { musics ->
                _uiState.update {
                    it.copy(myMusics = musics.take(count))
                }
            }
            .launchIn(viewModelScopeWithExceptionHandler)
    }

    override fun onClick(music: Music) {
        currentPlaylistUseCase.playMusic(music)
        viewModelScope.launch {
            _events.emit(MyPageEvent.NavigateToPlayerScreen)
        }
    }
}

sealed interface MyPageEvent {
    data class ShowMessage(val error: CtErrorType) : MyPageEvent
    data object NavigateToPlayerScreen : MyPageEvent
}