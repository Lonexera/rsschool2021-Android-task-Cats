package com.example.rs_school_task_5.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.rs_school_task_5.R
import com.example.rs_school_task_5.data.Cat
import com.example.rs_school_task_5.databinding.CatLayoutBinding

class CatAdapter(
    private val onClick: (cat: Cat) -> Unit
) : PagingDataAdapter<Cat, CatViewHolder>(itemComparator) {

    companion object {
        private val itemComparator = object : DiffUtil.ItemCallback<Cat>() {
            override fun areItemsTheSame(oldItem: Cat, newItem: Cat): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Cat, newItem: Cat): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CatViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = CatLayoutBinding.inflate(layoutInflater, parent, false)

        return CatViewHolder(binding, onClick)
    }

    override fun onBindViewHolder(holder: CatViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

class CatViewHolder(
    private val binding: CatLayoutBinding,
    private val onClick: (cat: Cat) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(cat: Cat?) {
        Glide.with(binding.root)
            .load(cat?.imageUrl)
            .placeholder(R.drawable.ic_baseline_image_24)
            .into(binding.catImage)

        if (cat != null) {
            binding.catImage.setOnClickListener {
                onClick(cat)
            }
        }
    }
}
