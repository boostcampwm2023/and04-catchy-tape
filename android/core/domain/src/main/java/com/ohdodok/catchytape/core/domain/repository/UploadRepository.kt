package com.ohdodok.catchytape.core.domain.repository

import kotlinx.coroutines.flow.Flow
import java.io.File

interface UploadRepository {

    fun uploadImage(uuid: String, file: File): Flow<String>

    fun uploadAudio(uuid: String, file: File): Flow<String>

}