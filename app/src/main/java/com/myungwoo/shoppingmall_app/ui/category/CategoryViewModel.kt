package com.myungwoo.shoppingmall_app.ui.category

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.myungwoo.shoppingmall_app.data.ProductModel
import com.myungwoo.shoppingmall_app.network.FirebaseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CategoryViewModel : ViewModel() {

    private val _categoryItems = MutableStateFlow<List<ProductModel>>(emptyList())
    val categoryItems: StateFlow<List<ProductModel>> = _categoryItems

    private val firebaseRepository = FirebaseRepository()

    fun fetchProductsByCategory(category: String) {
        viewModelScope.launch {
            kotlin.runCatching {
                firebaseRepository.getProductsByCategory(category)
            }.onSuccess { it ->
                _categoryItems.value = it.values.filter { it.category == category }
            }.onFailure {
                Log.e("CategoryViewModel-fetchProductsByCategory", "Error: ${it.message}", it)
            }
        }
    }
}