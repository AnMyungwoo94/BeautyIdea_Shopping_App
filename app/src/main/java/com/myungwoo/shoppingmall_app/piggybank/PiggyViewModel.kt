package com.myungwoo.shoppingmall_app.piggybank

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.zip
import kotlinx.coroutines.launch

class PiggyViewModel : ViewModel() {

    private val _buttonClicked = MutableSharedFlow<Unit>()

    private val emitTextFlow: Flow<Char> = flow {
        for (char in 'a'..'z') {
            emit(char)
        }
    }

    val zipFlow: Flow<Char> = _buttonClicked
        .zip(emitTextFlow) { _, char -> char }
        .map { char -> char.uppercaseChar() }

    fun onButtonClick() {
        viewModelScope.launch {
            _buttonClicked.emit(Unit)
        }
    }
}