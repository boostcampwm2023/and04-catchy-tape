package com.ohdodok.catchytape.core.domain.usecase.upload

class ValidateMusicTitleUseCase {

    operator fun invoke(title: String): Boolean {
        val regex = "(^[가-힣\\w]{1,}$)".toRegex()
        return regex.matches(title)
    }
}