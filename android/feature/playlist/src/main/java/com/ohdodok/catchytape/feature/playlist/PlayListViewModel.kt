package com.ohdodok.catchytape.feature.playlist

import androidx.lifecycle.ViewModel
import com.ohdodok.catchytape.core.domain.model.PlayListItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

data class PlayListUiState(
    // TODO: 임시 데이터이고 수정해야함
    val playList: List<PlayListItem> = listOf(
        PlayListItem(
            1,
            "최근 재생 목록",
            "~~~~",
            22
        )
    )
)

@HiltViewModel
class PlayListViewModel @Inject constructor(

) : ViewModel() {

    private val _uiState = MutableStateFlow(PlayListUiState())
    val uiState: StateFlow<PlayListUiState> = _uiState.asStateFlow()
}