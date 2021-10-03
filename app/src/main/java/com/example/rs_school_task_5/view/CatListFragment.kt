package com.example.rs_school_task_5.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.rs_school_task_5.data.CatViewModel
import com.example.rs_school_task_5.adapter.CatAdapter
import com.example.rs_school_task_5.adapter.CatLoaderStateAdapter
import com.example.rs_school_task_5.databinding.ListFragmentBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class CatListFragment : Fragment() {

    private var _binding: ListFragmentBinding? = null
    private val binding get() = _binding!!

    private val catsViewModel by viewModels<CatViewModel> { CatViewModel.Factory() }
    private lateinit var catAdapter: CatAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = ListFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val listener = activity as FragmentListener

        catAdapter = CatAdapter { cat ->
            lifecycleScope.launch {
                listener.openImageFragment(cat)
            }
        }

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
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

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                listener.onBack()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
