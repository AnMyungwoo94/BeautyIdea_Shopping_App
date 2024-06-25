package com.myungwoo.shoppingmall_app.paging.network.api

import com.myungwoo.shoppingmall_app.paging.network.model.DataItem
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("people")
    suspend fun getPeople(
        @Query("page") page: Int,
    ): List<DataItem>
}
