package com.example.rs_school_task_5

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rs_school_task_5.data.Cat
import com.example.rs_school_task_5.data.TheCatApiImpl
import kotlinx.coroutines.launch

class CatViewModel : ViewModel() {

    private val _items = MutableLiveData<List<Cat>>()
    val cats: LiveData<List<Cat>> get() = _items

    init {
        viewModelScope.launch {
            _items.value = TheCatApiImpl.getListOfCats(1)
        }
    }
}