package com.ohdodok.catchytape.feature.login

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow

class LoginViewModel : ViewModel() {

    private val _uiState: MutableStateFlow<LoginUiState> = MutableStateFlow(LoginUiState.Waiting)
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    private val _events: MutableSharedFlow<LoginEvent> = MutableSharedFlow<LoginEvent>()
    val events: SharedFlow<LoginEvent> = _events.asSharedFlow()

    fun login(token: String?, email: String?) {
        // 서버 통신 예정
    }
}


// TODO : 서버 연결 후 수정 될 예정
sealed class LoginUiState{
    data object Waiting : LoginUiState()
    data object Success : LoginUiState()
    data object Failure : LoginUiState()
}

// TODO : 서버 연결 후 수정 될 예정
sealed interface LoginEvent {
    data object NavigateToHome : LoginEvent
    data object NavigateToNickName : LoginEvent
}