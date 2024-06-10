package com.myungwoo.shoppingmall_app.network

import com.myungwoo.shoppingmall_app.data.ProductModel
import retrofit2.http.GET
import retrofit2.http.Query

interface FirebaseApi {
    @GET("product.json")
    suspend fun getProductsByCategory(
        @Query("category") category: String
    ): Result<Map<String, ProductModel>>
}
