package com.ohdodok.catchytape.core.domain.repository

import kotlinx.coroutines.flow.Flow
import java.io.File

interface StorageRepository {

    fun putImage(preSignedUrl: String, file: File): Flow<Unit>

    fun putAudio(preSignedUrl: String, file: File): Flow<Unit>

}