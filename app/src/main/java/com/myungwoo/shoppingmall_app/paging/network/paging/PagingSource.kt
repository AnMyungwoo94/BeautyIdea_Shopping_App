package com.myungwoo.shoppingmall_app.paging.network.paging

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.myungwoo.shoppingmall_app.paging.network.api.ApiService
import com.myungwoo.shoppingmall_app.paging.network.model.DataItem

private const val PAGE_SIZE = 50

class PagingSource(private val apiService: ApiService) : PagingSource<Int, DataItem>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, DataItem> {
        return try {
            Log.e("DataPagingSource", "페이지 생성")

            val nextPage = params.key ?: 0
            val response = apiService.getPeople(nextPage)

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

    override fun getRefreshKey(state: PagingState<Int, DataItem>): Int? {
        return state.anchorPosition
    }
}
