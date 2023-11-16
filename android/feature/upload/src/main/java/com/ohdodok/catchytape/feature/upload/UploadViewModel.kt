package com.ohdodok.catchytape.feature.upload

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class UploadViewModel : ViewModel() {

    val uploadedMusicTitle = MutableStateFlow<String>("")

    private val _uploadedMusicGenrePosition = MutableStateFlow(0)
    val uploadedMusicGenrePosition = _uploadedMusicGenrePosition.asStateFlow()

    private val _isUploadEnable = MutableStateFlow(false)
    val isUploadEnable = _isUploadEnable.asStateFlow()

    init {
        observeMusicTitle()
        observeGenrePosition()
    }

    val onChangePosition: (Int) -> Unit = { position: Int -> _uploadedMusicGenrePosition.value = position }


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
}