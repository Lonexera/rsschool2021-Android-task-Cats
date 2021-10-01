package com.example.rs_school_task_5

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.paging.*
import com.example.rs_school_task_5.data.Cat
import com.example.rs_school_task_5.data.CatService
import kotlinx.coroutines.flow.*

class CatViewModel : ViewModel() {

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    private var newPagingSource: PagingSource<*, *>? = null

    val cats: StateFlow<PagingData<Cat>> = query
        .map(::newPager)
        .flatMapLatest { pager -> pager.flow }
        .cachedIn(viewModelScope)
        .stateIn(viewModelScope, SharingStarted.Lazily, PagingData.empty())

    private fun newPager(query: String): Pager<Int, Cat> {
        return Pager(PagingConfig(10, enablePlaceholders = false)) {
            newPagingSource?.invalidate()
            CatsPagingSource(CatService).also { newPagingSource = it }
        }
    }

    @Suppress("UNCHECKED CAST")
    class Factory() : ViewModelProvider.Factory {

        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            require(modelClass == CatViewModel::class.java)
            return CatViewModel() as T
        }
    }
}
