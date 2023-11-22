package com.ohdodok.catchytape.feature.upload

import android.net.Uri
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import java.io.File
import javax.inject.Inject
import androidx.lifecycle.viewModelScope
import com.ohdodok.catchytape.core.domain.usecase.GetMusicGenresUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class UploadViewModel @Inject constructor(
    private val getMusicGenresUseCase: GetMusicGenresUseCase
) : ViewModel() {

    val musicTitle = MutableStateFlow("")
    val musicGenre = MutableStateFlow("")

    private val _thumbnailUrl: MutableStateFlow<String> = MutableStateFlow("")
    val thumbnailUrl = _thumbnailUrl.asStateFlow()

    private val _audioUrl: MutableStateFlow<String> = MutableStateFlow("")
    val audioUrl = _audioUrl.asStateFlow()

    val isUploadEnable: StateFlow<Boolean> =
        combine(musicTitle, musicGenre, thumbnailUrl, audioUrl) { title, genre, thumbnail, audio ->
            title.isNotBlank()
                    && genre.isNotBlank()
                    && thumbnail.isNotBlank()
                    && audio.isNotBlank()
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

    fun uploadImage(imageUri: Uri) {
        // todo : image 파일을 업로드 한다.
        // todo : 반환 값을 uploadedImage에 저장한다.
    }

    fun uploadAudio(audioUri: Uri) {
        // todo : audio 파일을 업로드 한다.
        val file = audioUri.path?.let { File(it) }
    }

}