package com.myungwoo.shoppingmall_app.piggybank

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class PiggyViewModel : ViewModel() {
    val buttonClicked: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }

    fun onButtonClick() {
        buttonClicked.value = true
    }
}