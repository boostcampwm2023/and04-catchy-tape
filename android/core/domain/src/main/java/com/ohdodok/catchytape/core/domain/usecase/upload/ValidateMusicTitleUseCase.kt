package com.ohdodok.catchytape.core.domain.usecase.upload

import javax.inject.Inject

class ValidateMusicTitleUseCase @Inject constructor(

) {

    operator fun invoke(title: String): Boolean {
        val regex = "(^[가-힣\\w]*$)".toRegex()
        return regex.matches(title)
    }
}