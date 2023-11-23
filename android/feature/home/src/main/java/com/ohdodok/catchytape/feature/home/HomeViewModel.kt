package com.ohdodok.catchytape.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ohdodok.catchytape.core.domain.model.Music
import com.ohdodok.catchytape.core.domain.usecase.GetRecentUploadedMusic
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import javax.inject.Inject

data class HomeUiState(
    val recentlyUploadedMusics: List<Music> = emptyList()
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getRecentUploadedMusicUseCase: GetRecentUploadedMusic
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        fetchUploadedMusics()
    }

    private fun fetchUploadedMusics() {
        getRecentUploadedMusicUseCase()
            .onEach { musics ->
                _uiState.update {
                    it.copy(
                        recentlyUploadedMusics = musics
                    )
                }
            }
            .launchIn(viewModelScope)
    }
}