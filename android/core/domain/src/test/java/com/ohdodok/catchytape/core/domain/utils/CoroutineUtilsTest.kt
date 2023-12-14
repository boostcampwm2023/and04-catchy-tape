package com.ohdodok.catchytape.core.domain.utils

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.test.runTest

class CoroutineUtilsKtTest : BehaviorSpec({

    given("throttleFirst 테스트하기 위해, delay(100)인 flow를 생성한다") {
        val testFlow = flow {
            repeat(3) { num ->
                emit(num)
                delay(100)
            }
        }

        `when`("windowDuration 을 400 만큼 주면") {
            val result = mutableListOf<Int>()
            runTest {
                testFlow.throttleFirst(400)
                    .onEach { result.add(it) }
                    .launchIn(this)
            }

            then("result는 [0]이 반환 된다.") { result shouldBe listOf(0) }
        }

        `when`("windowDuration 을 190 만큼 주면") {
            val result = mutableListOf<Int>()
            runTest {
                testFlow.throttleFirst(190)
                    .onEach { result.add(it) }
                    .launchIn(this)
            }

            then("result는 [0,2]이 반환 된다.") { result shouldBe listOf(0, 2) }
        }

        `when`("windowDuration 을 30 만큼 주면") {
            val result = mutableListOf<Int>()
            runTest {
                testFlow.throttleFirst(30)
                    .onEach { result.add(it) }
                    .launchIn(this)
            }

            then("result는 [0,1,2]이 반환 된다.") { result shouldBe listOf(0, 1, 2) }
        }
    }
})
