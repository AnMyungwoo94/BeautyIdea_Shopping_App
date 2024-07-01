package com.myungwoo.shoppingmall_app.paging.network.repository

import com.myungwoo.shoppingmall_app.paging.network.api.BeautyIdeaApiService
import com.myungwoo.shoppingmall_app.paging.network.model.PeopleData
import javax.inject.Inject

class NetworkRepository @Inject constructor(
    private val apiService: BeautyIdeaApiService
) {
    suspend fun getPeople(page: Int): List<PeopleData> {
        return apiService.getPeople(page).take(20)
    }
}