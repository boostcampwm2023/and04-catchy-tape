package com.ohdodok.catchytape.feature.upload

import android.net.Uri
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import java.io.File
import javax.inject.Inject
import androidx.lifecycle.viewModelScope
import com.ohdodok.catchytape.core.domain.usecase.UploadFileUseCase
import com.ohdodok.catchytape.core.domain.usecase.GetMusicGenresUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart

@HiltViewModel
class UploadViewModel @Inject constructor(
    private val getMusicGenresUseCase: GetMusicGenresUseCase,
    private val uploadFileUseCase: UploadFileUseCase
) : ViewModel() {

    val musicTitle = MutableStateFlow("")
    val musicGenre = MutableStateFlow("")

    private val _musicGenres: MutableStateFlow<List<String>> = MutableStateFlow(emptyList())
    val musicGenres = _musicGenres.asStateFlow()

    private val _uploadUiState: MutableStateFlow<UploadUiState> = MutableStateFlow(UploadUiState())
    val uploadUiState = _uploadUiState.asStateFlow()

    init {
        fetchGenres()
        observeTitle()
        observeGenre()
    }

    private fun fetchGenres() {
        getMusicGenresUseCase().onEach {
            _musicGenres.value = it
        }.launchIn(viewModelScope)
    }

    private fun observeTitle() {
        musicTitle.onEach {
            if (it.isEmpty()) {
                _uploadUiState.value = uploadUiState.value.copy(titleState = InputState.Empty)
                return@onEach
            } else {
                _uploadUiState.value =
                    uploadUiState.value.copy(titleState = InputState.Success(value = it))
            }
        }.launchIn(viewModelScope)
    }

    private fun observeGenre() {
        musicGenre.onEach {
            if (it.isEmpty()) {
                _uploadUiState.value = uploadUiState.value.copy(genreState = InputState.Empty)
                return@onEach
            } else {
                _uploadUiState.value =
                    uploadUiState.value.copy(genreState = InputState.Success(value = it))
            }
        }.launchIn(viewModelScope)
    }

    fun uploadImage(imageUri: Uri) {
        imageUri.path?.let { path ->
            uploadFileUseCase.getImgUrl(File(path)).onStart {
                _uploadUiState.value = uploadUiState.value.copy(imageState = InputState.Loading)
            }.catch {
                // TODO : 에러 처리
                _uploadUiState.value =
                    uploadUiState.value.copy(imageState = InputState.Error)
            }.onEach { url ->
                _uploadUiState.value =
                    uploadUiState.value.copy(imageState = InputState.Success(value = url))
            }.launchIn(viewModelScope)
        }
    }

    fun uploadAudio(audioUri: Uri) {
        audioUri.path?.let { path ->
            uploadFileUseCase.getAudioUrl(File(path)).onStart {
                _uploadUiState.value = uploadUiState.value.copy(audioState = InputState.Loading)
            }.catch {
                // TODO : 에러 처리
                _uploadUiState.value =
                    uploadUiState.value.copy(audioState = InputState.Error)
            }.onEach {
                _uploadUiState.value =
                    uploadUiState.value.copy(audioState = InputState.Success(value = it))
            }.launchIn(viewModelScope)
        }
    }
}

data class UploadUiState(
    val audioState: InputState = InputState.Empty,
    val imageState: InputState = InputState.Empty,
    val titleState: InputState = InputState.Empty,
    val genreState: InputState = InputState.Empty
)

sealed class InputState {
    data object Empty : InputState()

    data object Loading : InputState()
    data class Success(val value: String) : InputState()
    data object Error: InputState()
}

