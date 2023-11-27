package com.ohdodok.catchytape.core.data.repository

import com.ohdodok.catchytape.core.data.api.UploadApi
import com.ohdodok.catchytape.core.domain.repository.UuidRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class UuidRepositoryImpl @Inject constructor(
    private val uploadApi: UploadApi
) : UuidRepository {

    override fun getUuid(): Flow<String> = flow {
        emit(uploadApi.getUuid().uuid)
    }
}