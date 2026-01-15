/*
package com.nuecoo.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import com.bumptech.glide.Glide
import com.myren.core.ui.BaseListViewAdapter
import com.myren.core.ui.BaseViewHolder
import com.myren.domain.model.InsuranceItemData
import com.myren.new_safe_you.databinding.ItemInsuranceBinding
import com.nuecoo.core.ui.BaseViewHolder
import com.nuecoo.databinding.ItemMainNaviBinding

class MainNaviViewHolder(
    private val binding: ItemMainNaviBinding,
    private val onItemClick: (InsuranceItemData) -> Unit
) : BaseViewHolder<InsuranceItemData>(binding) {
    override fun bind(item: InsuranceItemData, position: Int) {
        binding.tvText.text = item.name
        Glide.with(binding.root.context).load(item.iconRes).centerInside().into(binding.ivItem)
        if (item.isSelected) {
            binding.ivChecked.visibility = View.VISIBLE
        } else {
            binding.ivChecked.visibility = View.GONE
        }
        binding.root.setOnClickListener {
            onItemClick(item)
        }
    }
}

class InsuranceAdapter(private val onItemClick: (InsuranceItemData) -> Unit) :
    BaseListViewAdapter<InsuranceItemData, InsuranceViewHolder>(object :
        DiffUtil.ItemCallback<InsuranceItemData>() {
        override fun areItemsTheSame(oldItem: InsuranceItemData, newItem: InsuranceItemData) =
            oldItem.hashCode() == newItem.hashCode()

        override fun areContentsTheSame(oldItem: InsuranceItemData, newItem: InsuranceItemData) =
            oldItem == newItem
    }) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = InsuranceViewHolder(
        ItemInsuranceBinding.inflate(LayoutInflater.from(parent.context), parent, false),
        onItemClick
    )
}*/
