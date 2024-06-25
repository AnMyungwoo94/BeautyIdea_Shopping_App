package com.myungwoo.shoppingmall_app.paging.network.paging

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.myungwoo.shoppingmall_app.paging.network.api.ApiService
import com.myungwoo.shoppingmall_app.paging.network.model.DataItem
import kotlinx.coroutines.flow.Flow

class PagingRepository(private val apiService: ApiService) {

    fun getPeople(): Flow<PagingData<DataItem>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                initialLoadSize = 40,
            ),
            pagingSourceFactory = { PagingSource(apiService) }
        ).flow
    }
}