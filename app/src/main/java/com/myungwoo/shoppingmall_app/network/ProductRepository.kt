package com.myungwoo.shoppingmall_app.network

import com.myungwoo.shoppingmall_app.data.ProductModel

class FirebaseRepository {

    private val firebaseApi = RetrofitInstance.retrofit.create(FirebaseApi::class.java)

    suspend fun getProductsByCategory(category: String): Result<Map<String, ProductModel>> {
        return firebaseApi.getProductsByCategory(category)
    }
}
