package com.ohdodok.catchytape.feature.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ohdodok.catchytape.core.domain.model.CtErrorType
import com.ohdodok.catchytape.core.domain.model.CtException
import com.ohdodok.catchytape.core.domain.usecase.AutomaticallyLoginUseCase
import com.ohdodok.catchytape.core.domain.usecase.LoginUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val automaticallyLoginUseCase: AutomaticallyLoginUseCase
) : ViewModel() {

    private val exceptionHandler = CoroutineExceptionHandler { cc, throwable ->
        viewModelScope.launch {
            if (throwable is CtException) {
                _events.emit(LoginEvent.ShowMessage(throwable.ctError))
            } else {
                _events.emit(LoginEvent.ShowMessage(CtErrorType.UN_KNOWN))
            }
            isAutoLoginFinished = true
        }
    }

    private val _events = MutableSharedFlow<LoginEvent>()
    val events = _events.asSharedFlow()

    var isAutoLoginFinished: Boolean = false
        private set

    fun login(token: String) {
        loginUseCase(token).onEach {
            _events.emit(LoginEvent.NavigateToHome)
        }.onCompletion { throwable ->
            if (throwable is CtException) {
                val ctError = throwable.ctError
                if (ctError == CtErrorType.NOT_EXIST_USER || ctError == CtErrorType.WRONG_TOKEN) {
                    _events.emit(LoginEvent.NavigateToNickName(token))
                }
            }
        }.launchIn(viewModelScope + exceptionHandler)
    }


    fun automaticallyLogin() {
        viewModelScope.launch(exceptionHandler) {
            val isLoggedIn = automaticallyLoginUseCase()
            if (isLoggedIn) _events.emit(LoginEvent.NavigateToHome)
            isAutoLoginFinished = true
        }
    }
}

sealed interface LoginEvent {
    data object NavigateToHome : LoginEvent
    data class NavigateToNickName(val googleToken: String) : LoginEvent
    data class ShowMessage(val error: CtErrorType) : LoginEvent
}