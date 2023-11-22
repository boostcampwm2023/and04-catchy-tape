package com.ohdodok.catchytape.feature.upload

import android.net.Uri
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import java.io.File
import javax.inject.Inject
import androidx.lifecycle.viewModelScope
import com.ohdodok.catchytape.core.domain.usecase.UploadFileUseCase
import com.ohdodok.catchytape.core.domain.usecase.GetMusicGenresUseCase
import com.ohdodok.catchytape.core.domain.usecase.UploadMusicUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class UploadViewModel @Inject constructor(
    private val getMusicGenresUseCase: GetMusicGenresUseCase,
    private val uploadFileUseCase: UploadFileUseCase,
    private val uploadMusicUseCase: UploadMusicUseCase
) : ViewModel() {

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
        scope = viewModelScope,
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
        }.stateIn(viewModelScope, SharingStarted.Eagerly, false)

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
        }.launchIn(viewModelScope)
    }

    fun uploadAudio(audioFile: File) {
        uploadFileUseCase.getAudioUrl(audioFile).onStart {
            _audioState.value = audioState.value.copy(isLoading = true)
        }.onEach { url ->
            _audioState.value = audioState.value.copy(url = url)
        }.onCompletion {
            _audioState.value = audioState.value.copy(isLoading = false)
        }.launchIn(viewModelScope)
    }

    fun uploadMusic() {
        if (isUploadEnable.value) {
            uploadMusicUseCase(
                imgUrl = imageState.value.url,
                audioUrl = audioState.value.url,
                title = musicTitle.value,
                genre = musicGenre.value
            ).onEach {
                // TODO : 업로드 성공
            }.catch {
                // TODO : 업로드 실패
            }.launchIn(viewModelScope)
        }
    }
}

data class UploadedFileState(
    val isLoading: Boolean = false,
    val url: String = ""
)

