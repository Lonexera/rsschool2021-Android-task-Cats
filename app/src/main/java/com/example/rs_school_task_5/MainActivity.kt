package com.example.rs_school_task_5

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.rs_school_task_5.adapter.CatAdapter
import com.example.rs_school_task_5.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val catsViewModel by viewModels<CatViewModel>()
    private val catAdapter = CatAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = catAdapter
        }

        catsViewModel.cats.observe(this, Observer {
            it ?: return@Observer
            catAdapter.addItems(it)
        })
    }
}