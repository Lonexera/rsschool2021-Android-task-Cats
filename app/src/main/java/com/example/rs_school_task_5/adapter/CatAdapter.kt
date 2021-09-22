package com.example.rs_school_task_5.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.example.rs_school_task_5.data.Cat
import com.example.rs_school_task_5.databinding.CatLayoutBinding

class CatAdapter : ListAdapter<Cat, CatViewHolder>(itemComparator) {


    companion object {
        val itemComparator = object : DiffUtil.ItemCallback<Cat>() {
            override fun areItemsTheSame(oldItem: Cat, newItem: Cat): Boolean {
                TODO("Not yet implemented")
            }

            override fun areContentsTheSame(oldItem: Cat, newItem: Cat): Boolean {
                TODO("Not yet implemented")
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CatViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = CatLayoutBinding.inflate(layoutInflater, parent, false)

        return CatViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CatViewHolder, position: Int) {
        holder.bind(getItem(position))
    }


}