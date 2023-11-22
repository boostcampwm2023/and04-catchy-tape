package com.ohdodok.catchytape.core.domain.usecase

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.File
import javax.inject.Inject

class GetFileUrlUseCase @Inject constructor(

) {
    operator fun invoke(file: File): Flow<String> = flow {
        // todo : file을 업로드 하고 url을 받아온다.
        // todo : 임시 방편
        delay(5000)
        emit("https://kr.object.ncloudstorage.com/catchy-tape-bucket2/music/2/%EC%9D%B4%EB%85%B8%EB%9E%98.mp3")
    }
}