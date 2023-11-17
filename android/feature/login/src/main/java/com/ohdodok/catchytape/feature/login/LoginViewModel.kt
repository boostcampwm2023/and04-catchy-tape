package com.ohdodok.catchytape.feature.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ohdodok.catchytape.core.domain.usecase.LoginUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase
) : ViewModel() {

    private val _uiState: MutableStateFlow<LoginUiState> = MutableStateFlow(LoginUiState.Waiting)
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<LoginEvent>()
    val events = _events.asSharedFlow()

    fun login(token: String?, email: String?) {
        if(token == null || email == null) {
            _uiState.value = LoginUiState.Failure
            return
        }else{
            loginUseCase(token)
                .onStart {
                    _uiState.value = LoginUiState.Waiting
                }
                .catch {
                    _uiState.value = LoginUiState.Failure
                    _events.emit(LoginEvent.NavigateToNickName(token))
                }
                .onEach {
                    _uiState.value = LoginUiState.Success
                    _events.emit(LoginEvent.NavigateToHome)
                }
                .launchIn(viewModelScope)
        }
    }
}


sealed class LoginUiState{
    data object Waiting : LoginUiState()
    data object Success : LoginUiState()
    data object Failure : LoginUiState()
}

sealed interface LoginEvent {
    data object NavigateToHome : LoginEvent
    data class NavigateToNickName(val googleToken: String) : LoginEvent
}