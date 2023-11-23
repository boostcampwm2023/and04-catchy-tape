package com.ohdodok.catchytape.feature.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ohdodok.catchytape.core.domain.usecase.AutomaticallyLoginUseCase
import com.ohdodok.catchytape.core.domain.usecase.LoginUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val automaticallyLoginUseCase: AutomaticallyLoginUseCase
) : ViewModel() {

    private val _events = MutableSharedFlow<LoginEvent>()
    val events = _events.asSharedFlow()

    var isAutoLoginFinished: Boolean = false
        private set

    fun login(token: String, isAutoLogin: Boolean = false) {
        loginUseCase(token)
            .catch {
                if (isAutoLogin.not()) {
                    _events.emit(LoginEvent.NavigateToNickName(token))
                }
            }.onEach {
                _events.emit(LoginEvent.NavigateToHome)
            }.launchIn(viewModelScope)
    }

    fun automaticallyLogin() {
        viewModelScope.launch {
            val isLoggedIn = automaticallyLoginUseCase()
            if (isLoggedIn) _events.emit(LoginEvent.NavigateToHome)
            isAutoLoginFinished = true
        }
    }
}

sealed interface LoginEvent {
    data object NavigateToHome : LoginEvent
    data class NavigateToNickName(val googleToken: String) : LoginEvent
}