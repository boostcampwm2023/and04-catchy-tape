package com.ohdodok.catchytape.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update

data class HomeUiState(
    val recentlyUploadedMusics: List<Unit> = emptyList()
)

class HomeViewModel constructor(
    // todo : DI로 주입하는 코드로 변경
    private val getMusicUseCase: GetMusicUseCase = GetMusicUseCase {
        flow { emit(listOf()) }
    },
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        fetchUploadedMusics()
    }

    private fun fetchUploadedMusics() {
        getMusicUseCase()
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

// todo : domain layer로 이동
fun interface GetMusicUseCase {
    operator fun invoke(): Flow<List<Unit>>
}
