package com.myungwoo.shoppingmall_app.paging.network.api

import com.myungwoo.shoppingmall_app.paging.network.model.PeopleData
import retrofit2.http.GET
import retrofit2.http.Query

interface BeautyIdeaApiService {
    @GET("people")
    suspend fun getPeople(
        @Query("page") page: Int,
    ): List<PeopleData>
}