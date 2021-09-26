package com.example.rs_school_task_5.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.rs_school_task_5.data.Cat
import com.example.rs_school_task_5.databinding.CatLayoutBinding

class CatAdapter : RecyclerView.Adapter<CatViewHolder>() {

    private val items = mutableListOf<Cat>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CatViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = CatLayoutBinding.inflate(layoutInflater, parent, false)

        return CatViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CatViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    fun addItems(newItems: List<Cat>) {
        items.addAll(newItems)
        notifyDataSetChanged()
    }

}