package com.ohdodok.catchytape.feature.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ohdodok.catchytape.core.domain.repository.AuthRepository
import com.ohdodok.catchytape.core.domain.usecase.signup.NicknameValidationResult
import com.ohdodok.catchytape.core.domain.usecase.signup.ValidateNicknameUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class NicknameViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val validateNicknameUseCase: ValidateNicknameUseCase,
) : ViewModel() {

    private val _events = MutableSharedFlow<NicknameEvent>()
    val events = _events.asSharedFlow()

    val nickname = MutableStateFlow("")

    val nicknameValidationState: StateFlow<NicknameValidationResult> =
        validateNicknameUseCase(nickname)
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(),
                NicknameValidationResult.EMPTY
            )

    fun signUp(googleToken: String) {
        if (nicknameValidationState.value != NicknameValidationResult.VALID) return

        authRepository.signUpWithGoogle(googleToken = googleToken, nickname = nickname.value)
            .onEach {
                _events.emit(NicknameEvent.NavigateToHome)
            }.launchIn(viewModelScope)
    }
}

sealed interface NicknameEvent {
    data object NavigateToHome : NicknameEvent
}