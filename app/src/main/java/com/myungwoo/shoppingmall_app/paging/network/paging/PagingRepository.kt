package com.myungwoo.shoppingmall_app.paging.network.paging

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.myungwoo.shoppingmall_app.paging.network.model.PeopleData
import com.myungwoo.shoppingmall_app.paging.network.repository.NetworkRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class PagingRepository @Inject constructor(
    private val networkRepository: NetworkRepository) {

    fun getPagingPeople(): Flow<PagingData<PeopleData>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                initialLoadSize = 40,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { PagingSource(networkRepository) }
        ).flow
    }
}