package com.ohdodok.catchytape.core.domain.usecase.signup

import com.ohdodok.catchytape.core.domain.usecase.upload.ValidateMusicTitleUseCase
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe

class MusicTitleValidationUseCaseTest : BehaviorSpec({

    val validateMusicTitleUseCase = ValidateMusicTitleUseCase()

    given("유효한 음악 제목이 주어지고") {
        `when`("유효성을 검사하면") {
            then("true를 반환한다") {
                val title = listOf("지", "지갑", "지갑에게", "LI", "LIVIN", "하이HI", "하2")
                title.forEach { title ->
                    validateMusicTitleUseCase(title) shouldBe true
                }
            }
        }
    }

    given("특수 문자가 포함된 음악 제목이 주어지고") {
        `when`("유효성을 검사하면") {
            then("false를 반환한다") {
                val title = listOf("못참아!", "ㄱ", ".", "못ㄱ")
                title.forEach { title ->
                    validateMusicTitleUseCase(title) shouldBe false
                }
            }
        }
    }
})