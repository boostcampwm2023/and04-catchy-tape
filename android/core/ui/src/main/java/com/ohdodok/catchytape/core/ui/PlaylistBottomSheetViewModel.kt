package com.ohdodok.catchytape.core.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ohdodok.catchytape.core.domain.model.CtErrorType
import com.ohdodok.catchytape.core.domain.model.CtException
import com.ohdodok.catchytape.core.domain.model.Playlist
import com.ohdodok.catchytape.core.domain.repository.PlaylistRepository
import com.ohdodok.catchytape.core.ui.model.PlaylistUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface PlaylistBottomSheetEvent {
    data object Close : PlaylistBottomSheetEvent
    data class ShowMessage(val error: CtErrorType) : PlaylistBottomSheetEvent
}

@HiltViewModel
class PlaylistBottomSheetViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val playlistRepository: PlaylistRepository,
) : ViewModel() {

    private val musicId: String = requireNotNull(savedStateHandle["musicId"]) {
        "musicId 정보가 누락되었어요."
    }

    private val _playlists = MutableStateFlow(emptyList<PlaylistUiModel>())
    val playlists: StateFlow<List<PlaylistUiModel>> = _playlists.asStateFlow()

    private val _events = MutableSharedFlow<PlaylistBottomSheetEvent>()
    val events: SharedFlow<PlaylistBottomSheetEvent> = _events.asSharedFlow()

    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        val errorType =
            if (throwable is CtException) throwable.ctError
            else CtErrorType.UN_KNOWN

        viewModelScope.launch { _events.emit(PlaylistBottomSheetEvent.ShowMessage(errorType)) }
    }

    init {
        fetchPlaylists()
    }

    private fun fetchPlaylists() {
        viewModelScope.launch {
            _playlists.value = playlistRepository.getPlaylists().first().toUiModels()
        }
    }

    private fun addMusicToPlaylist(playlistId: Int, musicId: String) {
        viewModelScope.launch(exceptionHandler) {
            playlistRepository.addMusicToPlaylist(playlistId = playlistId, musicId = musicId)
            _events.emit(PlaylistBottomSheetEvent.Close)
        }
    }

    private fun List<Playlist>.toUiModels(): List<PlaylistUiModel> = this.map { it.toUiModel() }

    private fun Playlist.toUiModel(): PlaylistUiModel {
        return PlaylistUiModel(
            id = id,
            title = title,
            thumbnailUrl = thumbnailUrl,
            trackSize = trackSize,
            onClick = {
                addMusicToPlaylist(
                    playlistId = id,
                    musicId = musicId
                )
            }
        )
    }
}