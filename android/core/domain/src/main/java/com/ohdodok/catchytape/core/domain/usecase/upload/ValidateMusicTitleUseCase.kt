package com.ohdodok.catchytape.core.domain.usecase.upload

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapLatest

class ValidateMusicTitleUseCase {

    operator fun invoke(title: Flow<String>): Flow<Boolean> = title.mapLatest { title ->
        val regex = "(^[가-힣\\w]{1,}$)".toRegex()
        regex.matches(title)
    }
}