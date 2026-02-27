package com.nuecoo.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import com.bumptech.glide.Glide
import com.nuecoo.core.ui.BaseListViewAdapter
import com.nuecoo.core.ui.BaseViewHolder
import com.nuecoo.databinding.ItemCollectionBinding
import com.nuecoo.databinding.ItemCollectionTypeBinding
import com.nuecoo.domain.model.CookieTypeData
import com.nuecoo.domain.model.CookieUIItemData

class CollectionTypeViewHolder(
    private val binding: ItemCollectionTypeBinding,
    private val onItemClick: (CookieTypeData) -> Unit
) : BaseViewHolder<CookieTypeData>(binding) {
    override fun bind(item: CookieTypeData, position: Int) {
        Glide.with(binding.root.context).load(item.imgRes).centerInside().into(binding.ivCookie)
        binding.root.setOnClickListener {
            onItemClick(item)
        }
    }
}

class CollectionTypeAdapter(private val onItemClick: (CookieTypeData) -> Unit) :
    BaseListViewAdapter<CookieTypeData, CollectionTypeViewHolder>(object :
        DiffUtil.ItemCallback<CookieTypeData>() {
        override fun areItemsTheSame(oldItem: CookieTypeData, newItem: CookieTypeData) =
            oldItem.hashCode() == newItem.hashCode()

        override fun areContentsTheSame(oldItem: CookieTypeData, newItem: CookieTypeData) =
            oldItem == newItem
    }) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = CollectionTypeViewHolder(
        ItemCollectionTypeBinding.inflate(LayoutInflater.from(parent.context), parent, false),
        onItemClick
    )
}