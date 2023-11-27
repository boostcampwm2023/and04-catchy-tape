package com.ohdodok.catchytape.core.domain.repository

import kotlinx.coroutines.flow.Flow
import java.io.File

interface UrlRepository {

    fun getImageUrl(file: File): Flow<String>

    fun getAudioUrl(file: File): Flow<String>

}