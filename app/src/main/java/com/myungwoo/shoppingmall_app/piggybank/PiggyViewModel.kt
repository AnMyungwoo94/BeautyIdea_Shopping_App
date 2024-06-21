package com.myungwoo.shoppingmall_app.piggybank

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.zip
import kotlinx.coroutines.launch

class PiggyViewModel : ViewModel() {

    private val _buttonClicked = MutableSharedFlow<Unit>()

    private val _textStream = MutableStateFlow('A')
    val textStream = _textStream.asStateFlow()

    private val emitTextFlow: Flow<Char> = flow {
        for (char in 'A'..'Z') {
            emit(char)
        }
    }

    init {
        startStream()
    }

    fun onButtonClick() {
        viewModelScope.launch {
            _buttonClicked.emit(Unit)
        }
    }

    private fun startStream() {
        viewModelScope.launch {
            _buttonClicked.zip(emitTextFlow) { _, char -> char }
                .collect { char ->
                    Log.d("PiggyViewModel", "char: $char")
                    _textStream.value = char
                }
        }
    }
}