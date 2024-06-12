package com.myungwoo.shoppingmall_app.ui.category

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.myungwoo.shoppingmall_app.data.ProductModel
import com.myungwoo.shoppingmall_app.network.api.FirebaseRepository
import com.myungwoo.shoppingmall_app.network.exception.ApiFailException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CategoryViewModel @Inject constructor(
    private val firebaseRepository: FirebaseRepository
) : ViewModel() {

    private val _categoryItems = MutableStateFlow<List<ProductModel>>(emptyList())
    val categoryItems: StateFlow<List<ProductModel>> = _categoryItems

    private val _errorMessageKey = MutableStateFlow<Int?>(null)
    val errorMessageKey: StateFlow<Int?> = _errorMessageKey

    fun fetchProductsByCategory(category: String) {
        viewModelScope.launch {
            firebaseRepository.getProductsByCategory(category)
                .onSuccess { items ->
                    _categoryItems.value = items.values.filter { it.category == category }
                }.onFailure { exception ->
                    if (exception is ApiFailException) {
                        _errorMessageKey.value = exception.getDisplayMessageKey()
                    } else {
                        Log.e(
                            "CategoryViewModel-fetchProductsByCategory",
                            "Error: ${exception.message}"
                        )
                    }
                }
        }
    }

    fun clearErrorMessage() {
        _errorMessageKey.value = null
    }
}