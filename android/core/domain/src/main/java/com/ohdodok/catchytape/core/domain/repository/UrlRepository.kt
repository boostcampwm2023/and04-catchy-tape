package com.ohdodok.catchytape.core.domain.repository

import kotlinx.coroutines.flow.Flow

interface UrlRepository {

    fun getImagePreSignedUrl(uuid: String): Flow<String>

    fun getAudioPreSignedUrl(uuid: String): Flow<String>

}