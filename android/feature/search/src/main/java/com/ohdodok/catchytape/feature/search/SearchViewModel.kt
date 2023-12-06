package com.ohdodok.catchytape.feature.search

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
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import javax.inject.Inject

data class SearchUiState(
    val searchedMusics: List<Music> = emptyList()
)


@HiltViewModel
class SearchViewModel @Inject constructor(
    private val musicRepository: MusicRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<SearchEvent>()
    val events: SharedFlow<SearchEvent> = _events.asSharedFlow()

    private val _keyword = MutableStateFlow("")
    val keyword: StateFlow<String> = _keyword.asStateFlow()

    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        val errorType =
            if (throwable is CtException) throwable.ctError
            else CtErrorType.UN_KNOWN

        viewModelScope.launch { _events.emit(SearchEvent.ShowMessage(errorType)) }
    }

    private val viewModelScopeWithExceptionHandler = viewModelScope + exceptionHandler

    init {
        observeUiState()
    }

    fun updateKeyword(newKeyword: String) {
        _keyword.update { newKeyword }
    }

    private fun observeUiState() {
        _keyword.debounce(300).filter {
            it.isNotBlank()
        }.onEach {
            fetchSearchedMusics(it)
        }.launchIn(viewModelScopeWithExceptionHandler)
    }

    private fun fetchSearchedMusics(keyword: String) {
        musicRepository.getSearchedMusics(keyword).onEach { musics ->
            _uiState.update { it.copy(searchedMusics = musics) }
        }.launchIn(viewModelScopeWithExceptionHandler)
    }
}


sealed interface SearchEvent {
    data class ShowMessage(val error: CtErrorType) : SearchEvent
}