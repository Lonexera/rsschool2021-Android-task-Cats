package com.example.rs_school_task_5.view

import com.example.rs_school_task_5.data.Cat

interface FragmentListener {
    fun openListFragment()
    fun openImageFragment(cat: Cat)
    fun onBack()
}
