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
            Log.e("DataPagingSource", "페이지 생성")
            val page = params.key ?: 0
            val peopleData =  networkRepository.getPeople(page)

            Log.e("DataPagingSource", "page: $nextPage")
            Log.e("DataPagingSource", "=====================")

            LoadResult.Page(
                data = response.take(20),
                prevKey = if (nextPage == 0) null else nextPage - 1,
                nextKey = if (response.isEmpty()) null else nextPage + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, PeopleData>): Int? {
        return state.anchorPosition
    }
}