package com.ohdodok.catchytape.core.domain.usecase.signup

import com.ohdodok.catchytape.core.domain.repository.AuthRepository
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class NicknameValidationUseCaseTest : BehaviorSpec() {
    private val authRepository: AuthRepository = mockk()
    private val validateNicknameUseCase = ValidateNicknameUseCase(authRepository)

    init {
        every {
            authRepository.isDuplicatedNickname(any())
        } returns flow { emit(false) }

        given("유효한 닉네임이 주어지고") {
            `when`("유효성을 검사하면") {
                then("Valid를 반환한다") {
                    // fixme : 화이트 박스 테스트임 (중복 검사에 대해서만 300ms를 기다리는데 이거 때문에 블랙 박스 테스트가 어려움)
                    // 현재 VALID 여부를 정확하게 테스트 하고 있기는 하다. (거짓 음성을 뱉지 않음)
                    val nicknameFlow = flowOf("아이유", "iu", "20", "가a1_.", "특수문자_.")
                    validateNicknameUseCase(nicknameFlow).onEach {
                        it shouldBe NicknameValidationResult.VALID
                    }.launchIn(this)
                }
            }
        }

        given("비어 있는 닉네임이 주어지고") {
            `when`("유효성을 검사하면") {
                then("Empty를 반환한다") {
                    validateNicknameUseCase(flowOf("")).onEach {
                        it shouldBe NicknameValidationResult.EMPTY
                    }.launchIn(this)
                }
            }
        }

        given("짧거나 긴 닉네임이 주어지고") {
            `when`("유효성을 검사하면") {
                then("Invalid length를 반환한다") {
                    val nicknameFlow = flowOf("한", "닉네임을이렇게길게지으면어떡해", "a")
                    validateNicknameUseCase(nicknameFlow).onEach {
                        it shouldBe NicknameValidationResult.INVALID_LENGTH
                    }.launchIn(this)
                }
            }
        }

        given("사용할 수 없는 문자가 포함된 닉네임이 주어지고") {
            `when`("유효성을 검사하면") {
                then("Invalid length를 반환한다") {
                    val nicknameFlow = flowOf("안 돼", "특수문자^", "특수문자*")
                    validateNicknameUseCase(nicknameFlow).onEach {
                        it shouldBe NicknameValidationResult.INVALID_CHARACTER
                    }.launchIn(this)
                }
            }
        }
    }
}