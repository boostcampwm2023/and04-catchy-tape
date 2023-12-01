package com.ohdodok.catchytape.core.domain.repository

import kotlinx.coroutines.flow.Flow

interface UuidRepository {

    fun getUuid(): Flow<String>
}