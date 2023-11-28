package com.ohdodok.catchytape.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ohdodok.catchytape.core.domain.model.CtErrorType
import com.ohdodok.catchytape.core.domain.model.CtException
import com.ohdodok.catchytape.core.domain.model.Music
import com.ohdodok.catchytape.core.domain.repository.MusicRepository
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

data class HomeUiState(
    val recentlyUploadedMusics: List<Music> = emptyList()
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val musicRepository: MusicRepository
) : ViewModel() {

    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        viewModelScope.launch {
            if (throwable is CtException) {
                _events.emit(HomeEvent.ShowMessage(throwable.ctError))
            } else {
                _events.emit(HomeEvent.ShowMessage(CtErrorType.UN_KNOWN))
            }
        }
    }

    private val viewModelScopeWithExceptionHandler = viewModelScope + exceptionHandler

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
}

sealed interface HomeEvent {
    data class ShowMessage(val error: CtErrorType) : HomeEvent
}