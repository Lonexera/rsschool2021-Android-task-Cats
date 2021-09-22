package com.example.rs_school_task_5.adapter

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.rs_school_task_5.data.Cat
import com.example.rs_school_task_5.databinding.CatLayoutBinding


class CatViewHolder(private val binding: CatLayoutBinding) : RecyclerView.ViewHolder(binding.root) {

    fun bind(cat: Cat) {
        Glide.with(binding.root).load(cat.imageUrl).into(binding.catImage)
    }
}