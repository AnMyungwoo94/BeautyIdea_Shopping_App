package com.myungwoo.shoppingmall_app.piggybank

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

class PiggyViewModel : ViewModel() {

    private val _buttonClicked = MutableStateFlow(false)
    val buttonClicked = _buttonClicked.asStateFlow()

    private val _textStream = MutableStateFlow('A')
    val textStream = _textStream.asStateFlow()

    fun onButtonClick() {
        _buttonClicked.value = true
        startStream()
    }

    private fun startStream() {
        viewModelScope.launch {
            flow {
                for (char in 'A'..'Z') {
                    emit(char)
                }
            }.collect { char ->
                _textStream.value = char
            }
        }
    }
}