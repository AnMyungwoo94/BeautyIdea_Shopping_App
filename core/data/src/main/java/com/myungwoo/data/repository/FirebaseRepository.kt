package com.myungwoo.data.repository

import com.myungwoo.model.ProductModel
import com.myungwoo.network.api.FirebaseApi
import javax.inject.Inject

class FirebaseRepository @Inject constructor(
    private val firebaseApi: FirebaseApi
) {
    suspend fun getProductsByCategory(category: String): Result<Map<String, ProductModel>> {
        return firebaseApi.getProductsByCategory(category)
    }
}