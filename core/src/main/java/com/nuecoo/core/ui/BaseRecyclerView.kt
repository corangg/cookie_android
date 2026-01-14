package com.nuecoo.core.ui

import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder

/**
 * BaseViewHolder는 RecyclerView.ViewHolder를 상속하는 추상 클래스입니다.
 * 데이터 바인딩을 위한 ViewDataBinding을 제네릭으로 받습니다.
 *
 * @param T ViewHolder가 바인딩할 아이템의 유형입니다.
 * @param VDB 데이터 바인딩을 위한 ViewDataBinding입니다.
 * @property binding ViewHolder의 루트 뷰에 대한 데이터 바인딩입니다.
 */
abstract class BaseViewHolder<T>(binding: ViewDataBinding) : ViewHolder(binding.root) {

    /**
     * ViewHolder에 데이터를 바인딩하는 추상 함수입니다.
     *
     * @param item ViewHolder에 바인딩할 아이템입니다.
     * @param position 아이템의 위치입니다.
     */
    abstract fun bind(item: T, position: Int)
}

/**
 * BaseListViewAdapter는 ListAdapter를 상속하는 추상 클래스입니다.
 * ListView의 아이템을 표시하기 위한 데이터 바인딩을 제공합니다.
 *
 * @param T Adapter가 표시할 아이템의 유형입니다.
 * @param VDB 데이터 바인딩을 위한 ViewDataBinding입니다.
 * @param VH BaseViewHolder를 상속하는 ViewHolder 클래스입니다.
 * @param diffCallback 아이템 간의 변경 사항을 비교하는 데 사용되는 DiffUtil.ItemCallback입니다.
 */
abstract class BaseListViewAdapter<T, VH : BaseViewHolder<T>>(
    diffCallback: DiffUtil.ItemCallback<T>
) : ListAdapter<T, VH>(diffCallback) {

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(getItem(position), position)
    }
}