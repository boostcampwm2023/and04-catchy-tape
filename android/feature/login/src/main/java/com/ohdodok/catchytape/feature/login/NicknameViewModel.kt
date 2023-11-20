package com.ohdodok.catchytape.feature.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ohdodok.catchytape.core.domain.signup.NicknameValidationResult
import com.ohdodok.catchytape.core.domain.signup.NicknameValidationUseCase
import com.ohdodok.catchytape.core.domain.usecase.SignUpUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

@HiltViewModel
class NicknameViewModel @Inject constructor(
    private val signUpUseCase: SignUpUseCase,
    private val nicknameValidationUseCase: NicknameValidationUseCase,
) : ViewModel() {

    private val _events = MutableSharedFlow<NicknameEvent>()
    val events = _events.asSharedFlow()

    val nickname = MutableStateFlow("")

    val nicknameValidationState: StateFlow<NicknameValidationResult> =
        nickname.map { nicknameValidationUseCase(it).single() }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(),
                NicknameValidationResult.EMPTY
            )

    fun signUp(googleToken: String) {

        if (nicknameValidationState.value != NicknameValidationResult.VALID) return

        signUpUseCase(googleToken = googleToken, nickname = nickname.value)
            .onEach {
                _events.emit(NicknameEvent.NavigateToHome)
            }.launchIn(viewModelScope)
    }
}

sealed interface NicknameEvent {
    data object NavigateToHome : NicknameEvent
}