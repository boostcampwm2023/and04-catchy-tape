package com.ohdodok.catchytape.feature.search

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
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SearchUiState(
    val searchedMusics: List<Music> = emptyList()
)

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val musicRepository: MusicRepository,
    private val currentPlaylistUseCase: CurrentPlaylistUseCase,
) : BaseViewModel(), MusicAdapter.Listener {
    override suspend fun onError(errorType: CtErrorType) {
        _events.emit(SearchEvent.ShowMessage(errorType))
    }

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<SearchEvent>()
    val events: SharedFlow<SearchEvent> = _events.asSharedFlow()

    private val _keyword = MutableStateFlow("")
    val keyword: StateFlow<String> = _keyword.asStateFlow()

    init {
        observeKeyword()
    }

    fun updateKeyword(newKeyword: String) {
        _keyword.update { newKeyword }
    }

    private fun observeKeyword() {
        _keyword.debounce(300)
            .onEach { fetchSearchedMusics(it) }
            .launchIn(viewModelScopeWithExceptionHandler)
    }

    private fun fetchSearchedMusics(keyword: String) {
        if (keyword.isBlank()) {
            _uiState.update { it.copy(searchedMusics = emptyList()) }
        } else {
            musicRepository.getSearchedMusics(keyword).onEach { musics ->
                _uiState.update { it.copy(searchedMusics = musics) }
            }.launchIn(viewModelScopeWithExceptionHandler)
        }
    }

    override fun onClick(music: Music) {
        viewModelScope.launch {
            currentPlaylistUseCase.playMusic(music)
            _events.emit(SearchEvent.NavigateToPlayerScreen)
        }
    }
}


sealed interface SearchEvent {
    data class ShowMessage(val error: CtErrorType) : SearchEvent
    data object NavigateToPlayerScreen : SearchEvent
}