package com.ohdodok.catchytape.core.domain.usecase.login

import com.ohdodok.catchytape.core.domain.repository.AuthRepository
import com.ohdodok.catchytape.core.domain.repository.UserTokenRepository
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.mockk

class AutomaticallyLoginUseCaseTest : BehaviorSpec({

    given("accessToken 을 얻을 수 있는 상황에서") {
        val authRepository: AuthRepository = mockk()
        val userTokenRepository: UserTokenRepository = mockk()
        val automaticallyLoginUseCase = AutomaticallyLoginUseCase(
            authRepository,
            userTokenRepository
        )

        `when`("accessToken이 Blank 이라면") {
            val blank = ""
            coEvery { userTokenRepository.getAccessToken() } returns blank
            coEvery { authRepository.verifyToken(blank) } returns false
            val result = automaticallyLoginUseCase.invoke()

            then("false 를 반환 한다") { result shouldBe false }
        }

        `when`("accessToken이 존재 한다면") {
            val validToken = "Bearer ~~"
            coEvery { userTokenRepository.getAccessToken() } returns validToken
            coEvery { authRepository.verifyToken(validToken) } returns true
            val result = automaticallyLoginUseCase.invoke()

            then("true 를 반환 한다") { result shouldBe true }
        }
    }
})
