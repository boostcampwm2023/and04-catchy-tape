package com.ohdodok.catchytape.feature.upload

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class UploadViewModel : ViewModel() {

    val uploadMusicTitle = MutableStateFlow<String>("")

    private val _uploadMusicGenrePosition = MutableStateFlow(0)
    val uploadMusicGenrePosition = _uploadMusicGenrePosition.asStateFlow()


    val onChangePosition: (Int) -> Unit = { position: Int -> _uploadMusicGenrePosition.value = position }
}