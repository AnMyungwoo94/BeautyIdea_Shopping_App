package com.myungwoo.shoppingmall_app.paging.network.paging

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.myungwoo.shoppingmall_app.paging.network.model.PeopleData
import com.myungwoo.shoppingmall_app.paging.network.repository.NetworkRepository
import javax.inject.Inject

class PagingSource @Inject constructor(private val networkRepository: NetworkRepository) : PagingSource<Int, PeopleData>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, PeopleData> {
        return try {
            val page = params.key ?: 0
            val peopleData =  networkRepository.getPeople(page)

            Log.d("DataPagingSource", "page: $page")

            LoadResult.Page(
                data = peopleData,
                prevKey = if (page == 0) null else page - 50,
                nextKey = if (peopleData.isEmpty()) null else page + 50
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, PeopleData>): Int? {
        return state.anchorPosition
    }
}