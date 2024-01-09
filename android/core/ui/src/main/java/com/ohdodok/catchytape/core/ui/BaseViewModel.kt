package com.ohdodok.catchytape.core.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ohdodok.catchytape.core.domain.model.CtErrorType
import com.ohdodok.catchytape.core.domain.model.CtException
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus

abstract class BaseViewModel : ViewModel() {
    abstract suspend fun onError(errorType: CtErrorType)

    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        val errorType =
            if (throwable is CtException) throwable.ctError
            else CtErrorType.UN_KNOWN

        viewModelScope.launch { onError(errorType) }
    }

    protected val viewModelScopeWithExceptionHandler = viewModelScope + exceptionHandler
}