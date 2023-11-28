package com.ohdodok.catchytape.feature.mypage

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject


@HiltViewModel
class MyPageViewModel @Inject constructor() : ViewModel() {
    private val _events = MutableSharedFlow<MyPageEvent>()
    val events: SharedFlow<MyPageEvent> = _events.asSharedFlow()
}


sealed interface MyPageEvent {

}