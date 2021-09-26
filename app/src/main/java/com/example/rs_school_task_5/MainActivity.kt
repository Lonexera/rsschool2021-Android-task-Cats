package com.example.rs_school_task_5

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.rs_school_task_5.adapter.CatAdapter
import com.example.rs_school_task_5.adapter.CatLoaderStateAdapter
import com.example.rs_school_task_5.databinding.ActivityMainBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.coroutines.coroutineContext

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val catsViewModel by viewModels<CatViewModel> { CatViewModel.Factory() }
    private val catAdapter by lazy(LazyThreadSafetyMode.NONE) {
        CatAdapter()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = catAdapter.withLoadStateHeaderAndFooter(
                header = CatLoaderStateAdapter(),
                footer = CatLoaderStateAdapter()
            )
        }

        catAdapter.addLoadStateListener { state ->
            with(binding) {
                recyclerView.isVisible = state.refresh != LoadState.Loading
                progressBar.isVisible = state.refresh == LoadState.Loading
            }
        }

        lifecycleScope.launch {
            catsViewModel.cats.collectLatest {
                catAdapter.submitData(it)
            }
        }
    }
}