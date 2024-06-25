package com.myungwoo.shoppingmall_app.paging.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.myungwoo.shoppingmall_app.paging.network.model.DataItem
import com.myungwoo.shoppingmall_app.paging.network.paging.PagingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class PagingViewModel @Inject constructor(repository: PagingRepository) : ViewModel() {

    val people: Flow<PagingData<DataItem>> = repository.getPeople().cachedIn(viewModelScope)
}