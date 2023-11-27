package com.ohdodok.catchytape.feature.upload

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ohdodok.catchytape.core.domain.model.CtErrorType
import com.ohdodok.catchytape.core.domain.model.CtException
import com.ohdodok.catchytape.core.domain.usecase.GetMusicGenresUseCase
import com.ohdodok.catchytape.core.domain.usecase.UploadFileUseCase
import com.ohdodok.catchytape.core.domain.usecase.UploadMusicUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import java.io.File
import javax.inject.Inject

@HiltViewModel
class UploadViewModel @Inject constructor(
    private val getMusicGenresUseCase: GetMusicGenresUseCase,
    private val uploadFileUseCase: UploadFileUseCase,
    private val uploadMusicUseCase: UploadMusicUseCase
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

    private val viewModelScopeWithExceptionHandler = viewModelScope + exceptionHandler

    val musicTitle = MutableStateFlow("")
    val musicGenre = MutableStateFlow("")

    private val _imageState: MutableStateFlow<UploadedFileState> =
        MutableStateFlow(UploadedFileState())
    val imageState = _imageState.asStateFlow()

    private val _audioState: MutableStateFlow<UploadedFileState> =
        MutableStateFlow(UploadedFileState())
    val audioState = _audioState.asStateFlow()

    val isLoading: StateFlow<Boolean> = combine(imageState, audioState) { imageState, audioState ->
        imageState.isLoading || audioState.isLoading
    }.stateIn(
        scope = viewModelScopeWithExceptionHandler,
        started = SharingStarted.Eagerly,
        initialValue = false
    )

    val isUploadEnable: StateFlow<Boolean> =
        combine(
            musicTitle, musicGenre, imageState, audioState
        ) { title, genre, imageState, audioState ->
            title.isNotBlank()
                    && genre.isNotBlank()
                    && imageState.url.isNotBlank()
                    && audioState.url.isNotBlank()
        }.stateIn(viewModelScopeWithExceptionHandler, SharingStarted.Eagerly, false)

    private val _musicGenres: MutableStateFlow<List<String>> = MutableStateFlow(emptyList())
    val musicGenres = _musicGenres.asStateFlow()

    init {
        fetchGenres()
    }

    private fun fetchGenres() {
        getMusicGenresUseCase().onEach {
            _musicGenres.value = it
        }.launchIn(viewModelScope)
    }

    fun uploadImage(imageFile: File) {
        uploadFileUseCase.getImgUrl(imageFile).onStart {
            _imageState.value = imageState.value.copy(isLoading = true)
        }.onEach { url ->
            _imageState.value = imageState.value.copy(url = url)
        }.onCompletion {
            _imageState.value = imageState.value.copy(isLoading = false)
        }.launchIn(viewModelScopeWithExceptionHandler)
    }

    fun uploadAudio(audioFile: File) {
        uploadFileUseCase.getAudioUrl(audioFile).onStart {
            _audioState.value = audioState.value.copy(isLoading = true)
        }.onEach { url ->
            _audioState.value = audioState.value.copy(url = url)
        }.onCompletion {
            _audioState.value = audioState.value.copy(isLoading = false)
        }.launchIn(viewModelScopeWithExceptionHandler)
    }

    fun uploadMusic() {
        if (isUploadEnable.value) {
            uploadMusicUseCase(
                imageUrl = imageState.value.url,
                audioUrl = audioState.value.url,
                title = musicTitle.value,
                genre = musicGenre.value
            ).onEach {
                _events.emit(UploadEvent.NavigateToBack)
            }.launchIn(viewModelScopeWithExceptionHandler)
        }
    }
}

data class UploadedFileState(
    val isLoading: Boolean = false,
    val url: String = ""
)

sealed interface UploadEvent {
    data object NavigateToBack : UploadEvent
    data class ShowMessage(val error: CtErrorType) : UploadEvent
}
