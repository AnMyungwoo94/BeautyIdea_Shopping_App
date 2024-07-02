package com.myungwoo.shoppingmall_app.paging.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.myungwoo.shoppingmall_app.paging.network.model.PeopleData
import com.myungwoo.shoppingmall_app.paging.network.paging.PagingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class PagingViewModel @Inject constructor(pagingRepository: PagingRepository) : ViewModel() {

    val people: Flow<PagingData<PeopleData>> = pagingRepository.getPagingPeople().cachedIn(viewModelScope)
}