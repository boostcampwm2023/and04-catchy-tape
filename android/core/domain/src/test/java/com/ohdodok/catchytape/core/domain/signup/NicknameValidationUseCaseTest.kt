package com.ohdodok.catchytape.core.domain.signup

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe

class NicknameValidationUseCaseTest : BehaviorSpec() {
    private val sut = NicknameValidationUseCase()

    init {
        given("유효한 닉네임이 주어지고") {
            `when`("유효성을 검사하면") {
                then("Valid를 반환한다") {
                    sut("아이유") shouldBe NicknameValidationResult.VALID
                    sut("아이유") shouldBe NicknameValidationResult.VALID
                }


                then("Valid를 반환한다") {
                    sut("아이유") shouldBe NicknameValidationResult.VALID
                    sut("아이유") shouldBe NicknameValidationResult.VALID
                }
            }

            `when`("유효성을 검사하면2") {
                then("Valid를 반환한다") {
                    sut("아이유") shouldBe NicknameValidationResult.VALID
                    sut("아이유") shouldBe NicknameValidationResult.VALID
                }


                then("Valid를 반환한다") {
                    sut("아이유") shouldBe NicknameValidationResult.VALID
                    sut("아이유") shouldBe NicknameValidationResult.VALID
                }
            }
        }
    }
}