package com.ohdodok.catchytape.feature.upload

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ohdodok.catchytape.core.domain.model.CtErrorType
import com.ohdodok.catchytape.core.domain.model.CtException
import com.ohdodok.catchytape.core.domain.repository.MusicRepository
import com.ohdodok.catchytape.core.domain.repository.UuidRepository
import com.ohdodok.catchytape.core.domain.usecase.upload.UploadFileUseCase
import com.ohdodok.catchytape.core.domain.usecase.upload.UploadMusicUseCase
import com.ohdodok.catchytape.core.domain.usecase.upload.ValidateMusicTitleUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.retry
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import java.io.File
import javax.inject.Inject

data class UploadUiState(
    val musicTitleState: MusicTitleState = MusicTitleState(),
    val musicGenre: String = "",
    val imageState: UploadedFileState = UploadedFileState(),
    val audioState: UploadedFileState = UploadedFileState(),
    val musicGenres: List<String> = emptyList(),
) {
    val isLoading: Boolean
        get() = imageState.isLoading || audioState.isLoading

    val isUploadEnable: Boolean
        get() = musicTitleState.title.isNotBlank() && musicTitleState.isValid
                && musicGenre.isNotBlank()
                && imageState.url.isNotBlank()
                && audioState.url.isNotBlank()
}

data class UploadedFileState(
    val isLoading: Boolean = false,
    val url: String = ""
)

data class MusicTitleState(
    val title: String = "",
    val isValid: Boolean = true
)

sealed interface UploadEvent {
    data object NavigateToBack : UploadEvent
    data class ShowMessage(val error: CtErrorType) : UploadEvent
}


@HiltViewModel
class UploadViewModel @Inject constructor(
    private val musicRepository: MusicRepository,
    private val uploadFileUseCase: UploadFileUseCase,
    private val uploadMusicUseCase: UploadMusicUseCase,
    private val validateMusicTitleUseCase: ValidateMusicTitleUseCase,
    private val uuidRepository: UuidRepository,
) : ViewModel() {

    private val _events = MutableSharedFlow<UploadEvent>()
    val events = _events.asSharedFlow()

    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        viewModelScope.launch {
            if (throwable is CtException) {
                _events.emit(UploadEvent.ShowMessage(throwable.ctError))
            } else {
                _events.emit(UploadEvent.ShowMessage(CtErrorType.UN_KNOWN))
            }
        }
    }

    private lateinit var uuid: String

    private val viewModelScopeWithExceptionHandler = viewModelScope + exceptionHandler

    private val _uiState: MutableStateFlow<UploadUiState> = MutableStateFlow(UploadUiState())
    val uiState: StateFlow<UploadUiState> = _uiState.asStateFlow()

    init {
        fetchGenres()
        generateUuid()
    }

    private fun fetchGenres() {
        musicRepository.getGenres().onEach { genres ->
            _uiState.update { it.copy(musicGenres = genres) }
        }.launchIn(viewModelScope)
    }

    private fun generateUuid() {
        uuidRepository.getUuid().onEach { generatedUuid ->
            uuid = generatedUuid
        }.retry(3)
            .launchIn(viewModelScopeWithExceptionHandler)
    }

    fun updateMusicTitle(title: CharSequence) {
        _uiState.update {
            it.copy(
                musicTitleState = it.musicTitleState.copy(
                    title = title.toString(),
                    isValid = validateMusicTitleUseCase(title.toString())
                )
            )
        }
    }

    fun updateMusicGenre(genre: CharSequence) {
        _uiState.update { it.copy(musicGenre = genre.toString()) }
    }

    fun uploadImage(imageFile: File) {
        uploadFileUseCase.uploadMusicCover(imageFile, uuid).onStart {
            _uiState.update { it.copy(imageState = it.imageState.copy(isLoading = true)) }
        }.onEach { url ->
            _uiState.update { it.copy(imageState = it.imageState.copy(url = url)) }
        }.onCompletion {
            _uiState.update { it.copy(imageState = it.imageState.copy(isLoading = false)) }
        }.launchIn(viewModelScopeWithExceptionHandler)
    }

    fun uploadAudio(audioFile: File) {
        uploadFileUseCase.uploadAudio(audioFile, uuid).onStart {
            _uiState.update { it.copy(audioState = it.audioState.copy(isLoading = true)) }
        }.onEach { url ->
            _uiState.update { it.copy(audioState = it.audioState.copy(url = url)) }
        }.onCompletion {
            _uiState.update { it.copy(audioState = it.audioState.copy(isLoading = false)) }
        }.launchIn(viewModelScopeWithExceptionHandler)
    }

    fun uploadMusic() {
        if (!uiState.value.isUploadEnable) return

        uploadMusicUseCase(
            imageUrl = uiState.value.imageState.url,
            audioUrl = uiState.value.audioState.url,
            title = uiState.value.musicTitleState.title,
            genre = uiState.value.musicGenre
        ).onEach {
            _events.emit(UploadEvent.NavigateToBack)
        }.launchIn(viewModelScopeWithExceptionHandler)
    }
}
