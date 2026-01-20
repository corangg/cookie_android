package com.nuecoo.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import com.bumptech.glide.Glide
import com.nuecoo.core.ui.BaseListViewAdapter
import com.nuecoo.core.ui.BaseViewHolder
import com.nuecoo.databinding.ItemCookieBinding
import com.nuecoo.domain.model.CookieUIItemData

class CookieViewHolder(
    private val binding: ItemCookieBinding,
    private val onItemClick: (CookieUIItemData) -> Unit
) : BaseViewHolder<CookieUIItemData>(binding) {
    override fun bind(item: CookieUIItemData, position: Int) {
        Glide.with(binding.root.context).load(item.imgRes).centerInside().into(binding.ivCookie)
        binding.root.setOnClickListener {
            onItemClick(item)
        }
    }
}

class CookieAdapter(private val onItemClick: (CookieUIItemData) -> Unit) :
    BaseListViewAdapter<CookieUIItemData, CookieViewHolder>(object :
        DiffUtil.ItemCallback<CookieUIItemData>() {
        override fun areItemsTheSame(oldItem: CookieUIItemData, newItem: CookieUIItemData) =
            oldItem.hashCode() == newItem.hashCode()

        override fun areContentsTheSame(oldItem: CookieUIItemData, newItem: CookieUIItemData) =
            oldItem == newItem
    }) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = CookieViewHolder(
        ItemCookieBinding.inflate(LayoutInflater.from(parent.context), parent, false),
        onItemClick
    )
}
