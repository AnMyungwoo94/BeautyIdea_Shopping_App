package com.myungwoo.shoppingmall_app.piggybank

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.zip
import kotlinx.coroutines.launch

class PiggyViewModel : ViewModel() {

    private val _buttonClicked = MutableSharedFlow<Unit>()

    private val emitTextFlow: Flow<Char> = flow {
        for (char in 'A'..'Z') {
            emit(char)
        }
    }

    val zipFlow = _buttonClicked.zip(emitTextFlow) { _, char -> char }

    fun onButtonClick() {
        viewModelScope.launch {
            _buttonClicked.emit(Unit)
        }
    }
}