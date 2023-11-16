package com.ohdodok.catchytape.feature.upload

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import java.io.File
import javax.inject.Inject

@HiltViewModel
class UploadViewModel @Inject constructor(

) : ViewModel() {

    private var uploadedImage: String? = null

    fun uploadImage(imageFile: File) {
        // todo : image 파일을 업로드 한다.
        // todo : 반환 값을 uploadedImage에 저장한다.
    }

    fun uploadAudio(audioFile: File) {
        // todo : audio 파일을 업로드 한다.
    }
}