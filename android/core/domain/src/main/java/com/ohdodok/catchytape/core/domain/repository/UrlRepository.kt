package com.ohdodok.catchytape.core.domain.repository

import kotlinx.coroutines.flow.Flow
import java.io.File

interface UrlRepository {

    fun getUuid(): Flow<String>

    fun getImageUrl(uuid: String, file: File): Flow<String>

    fun getAudioUrl(uuid: String, file: File): Flow<String>

}