package com.ohdodok.catchytape.feature.upload

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import java.io.File
import javax.inject.Inject
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@HiltViewModel
class UploadViewModel @Inject constructor(

) : ViewModel() {

    private var uploadedImage: String? = null

    val uploadedMusicTitle = MutableStateFlow<String>("")

    private val _uploadedMusicGenrePosition = MutableStateFlow(0)
    val uploadedMusicGenrePosition = _uploadedMusicGenrePosition.asStateFlow()

    private val _isUploadEnable = MutableStateFlow(false)
    val isUploadEnable = _isUploadEnable.asStateFlow()
    
    val onChangePosition: (Int) -> Unit = { position: Int -> _uploadedMusicGenrePosition.value = position }

    init {
        observeMusicTitle()
        observeGenrePosition()
    }

    private fun observeMusicTitle() {
        uploadedMusicTitle.onEach {
            _isUploadEnable.value = uploadedMusicGenrePosition.value != 0 && it.isNotEmpty()
        }.launchIn(viewModelScope)
    }

    private fun observeGenrePosition() {
        uploadedMusicGenrePosition.onEach {
            _isUploadEnable.value = it != 0 && uploadedMusicTitle.value.isNotEmpty()
        }.launchIn(viewModelScope)
    }
    
    fun uploadImage(imageFile: File) {
        // todo : image 파일을 업로드 한다.
        // todo : 반환 값을 uploadedImage에 저장한다.
    }

    fun uploadAudio(audioFile: File) {
        // todo : audio 파일을 업로드 한다.
    }
}