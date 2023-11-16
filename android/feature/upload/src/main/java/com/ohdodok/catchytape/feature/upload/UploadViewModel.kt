package com.ohdodok.catchytape.feature.upload

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import java.io.File
import javax.inject.Inject
import androidx.lifecycle.viewModelScope
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

) : ViewModel() {

    private var uploadedImage: String? = null

    val uploadedMusicTitle = MutableStateFlow("")

    private val _uploadedMusicGenrePosition = MutableStateFlow(0)
    val uploadedMusicGenrePosition = _uploadedMusicGenrePosition.asStateFlow()

    val isUploadEnable: StateFlow<Boolean> =
        combine(uploadedMusicTitle, uploadedMusicGenrePosition) { title, genrePosition ->
            title.isNotBlank() && genrePosition != 0
        }.stateIn(viewModelScope, SharingStarted.Eagerly, false)

    val onChangePosition: (Int) -> Unit = { position: Int -> _uploadedMusicGenrePosition.value = position }

    fun uploadImage(imageFile: File) {
        // todo : image 파일을 업로드 한다.
        // todo : 반환 값을 uploadedImage에 저장한다.
    }

    fun uploadAudio(audioFile: File) {
        // todo : audio 파일을 업로드 한다.
    }
}