package com.myungwoo.shoppingmall_app.piggybank

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.scan
import kotlinx.coroutines.launch

class PiggyViewModel : ViewModel() {

    private val inputTextFlow = MutableSharedFlow<String>()

    val emitIntFlow: Flow<Int> = inputTextFlow
        .filter { it.toIntOrNull() != null}
        .map { it.toInt() }
        .scan(0) { total, value -> total + value }

    fun onButtonClick(text: String) {
        viewModelScope.launch {
            inputTextFlow.emit(text)
        }
    }
}