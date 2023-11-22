package com.ohdodok.catchytape.core.domain.usecase

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.File
import javax.inject.Inject

class UploadFileUseCase @Inject constructor(

) {

    fun getImgUrl(file: File): Flow<String> = flow {
        // todo : 서버 기다리는 중..
        delay(1000)
        emit("https://kr.object.ncloudstorage.com/catchy-tape-bucket2/image/%EC%8A%A4%ED%81%AC%EB%A6%B0%EC%83%B7%202023-11-21%20180100.png")
    }

    fun getAudioUrl(file: File): Flow<String> = flow {
        // todo : 서버 기다리는 중..
        delay(1000)
        emit("https://kr.object.ncloudstorage.com/catchy-tape-bucket2/music/2/%EC%9D%B4%EB%85%B8%EB%9E%98.mp3")
    }
}