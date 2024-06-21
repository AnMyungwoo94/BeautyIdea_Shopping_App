package com.myungwoo.shoppingmall_app.piggybank

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

class PiggyViewModel : ViewModel() {

    private val _buttonClicked = MutableSharedFlow<Unit>()

    val emitTextFlow: Flow<Char> = flow {
        for (char in 'a'..'z') {
            emit(char)
            delay(1000L)
        }
    }

    fun onButtonClick() {
        viewModelScope.launch {
            _buttonClicked.emit(Unit)
        }
    }
}