package com.ohdodok.catchytape.feature.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ohdodok.catchytape.core.domain.usecase.SignUpUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class NicknameViewModel @Inject constructor(
    private val signUpUseCase: SignUpUseCase
) : ViewModel() {

    private val _events = MutableSharedFlow<NicknameEvent>()
    val events = _events.asSharedFlow()

    val nickname = MutableStateFlow("")

    fun signUp(googleToken: String) {
        // TODO : 중복 검사 및 유효성 검사
        signUpUseCase(googleToken = googleToken, nickname = nickname.value)
            .onEach {
                _events.emit(NicknameEvent.NavigateToHome)
            }.launchIn(viewModelScope)
    }
}

sealed interface NicknameEvent {
    data object NavigateToHome : NicknameEvent
}