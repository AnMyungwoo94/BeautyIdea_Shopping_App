package com.myungwoo.shoppingmall_app.network.api

import com.myungwoo.shoppingmall_app.data.ProductModel
import javax.inject.Inject

class FirebaseRepository @Inject constructor(
    private val firebaseApi: FirebaseApi
) {
    suspend fun getProductsByCategory(category: String): Result<Map<String, ProductModel>> {
        return firebaseApi.getProductsByCategory(category)
    }
}