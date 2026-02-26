package com.nuecoo.ui.fragment

import android.R.string.no
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.map
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.nuecoo.R
import com.nuecoo.core.ui.BaseFragment
import com.nuecoo.databinding.FragmentCollectionBinding
import com.nuecoo.databinding.FragmentOvenBinding
import com.nuecoo.databinding.FragmentOvenBinding.inflate
import com.nuecoo.domain.model.CookieItemData
import com.nuecoo.domain.model.CookieUIItemData
import com.nuecoo.domain.model.DailyCookieItemData
import com.nuecoo.mapper.toUiItem
import com.nuecoo.ui.adapter.CollectionAdapter
import com.nuecoo.ui.adapter.CookieAdapter
import com.nuecoo.ui.util.CustomToast
import com.nuecoo.viewmodel.CollectionFragmentViewModel
import com.nuecoo.viewmodel.OvenFragmentViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlin.collections.map
import kotlin.getValue

@AndroidEntryPoint
class CollectionFragment : BaseFragment<FragmentCollectionBinding>(FragmentCollectionBinding::inflate) {
    private val viewModel: CollectionFragmentViewModel by viewModels()

    private var collectionAdapter: CollectionAdapter? = null

    override fun setUi() {
        binding.viewModel = viewModel
        bindingOnClick()
        setAdapter()
    }

    override fun setUpDate() {
    }

    override fun setObserve(lifecycleOwner: LifecycleOwner) {
        viewModel.cookieList.observe(lifecycleOwner, ::updateList)
    }

    private fun bindingOnClick() {
        /*binding.rvCollection.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val offsetPx = recyclerView.computeVerticalScrollOffset() // 현재 스크롤된 px
                offsetPx
                val isBottom = !binding.rvCollection.canScrollVertically(1)
                if(offsetPx<200){
                    binding.rvCollection.setBackgroundResource(R.drawable.img_collection_background_top)
                }else if(offsetPx>200){
                    binding.rvCollection.setBackgroundResource(R.drawable.img_collection_background_mid)
                }
                // offsetPx 사용
            }
        })*/
    }

    private fun setAdapter() {
        binding.rvCollection.run {
            layoutManager = GridLayoutManager(requireContext(), 2)
            collectionAdapter = CollectionAdapter{

            }
            adapter = collectionAdapter
        }
    }

    private fun updateList(items: List<DailyCookieItemData>) {
        //val uiItems = items.map { it.toUiItem() }

        val testList = listOf<CookieUIItemData>(
            CookieUIItemData(
                type = 1,
                no = 1,
                isOpened = true,
                imgRes = R.drawable.img_cookie_cheering_6
            ),
            CookieUIItemData(
                type = 1,
                no = 1,
                isOpened = true,
                imgRes = R.drawable.img_cookie_cheering_6
            ),
            CookieUIItemData(
                type = 1,
                no = 1,
                isOpened = true,
                imgRes = R.drawable.img_cookie_cheering_6
            ),
            CookieUIItemData(
                type = 1,
                no = 1,
                isOpened = true,
                imgRes = R.drawable.img_cookie_cheering_6
            ),
            CookieUIItemData(
                type = 1,
                no = 1,
                isOpened = true,
                imgRes = R.drawable.img_cookie_cheering_6
            ),
            CookieUIItemData(
                type = 1,
                no = 1,
                isOpened = true,
                imgRes = R.drawable.img_cookie_cheering_6
            ),
            CookieUIItemData(
                type = 1,
                no = 1,
                isOpened = true,
                imgRes = R.drawable.img_cookie_cheering_6
            ),
            CookieUIItemData(
                type = 1,
                no = 1,
                isOpened = true,
                imgRes = R.drawable.img_cookie_cheering_6
            ),
            CookieUIItemData(
                type = 1,
                no = 1,
                isOpened = true,
                imgRes = R.drawable.img_cookie_cheering_6
            ),

        )
        collectionAdapter?.submitList(testList)
    }

}