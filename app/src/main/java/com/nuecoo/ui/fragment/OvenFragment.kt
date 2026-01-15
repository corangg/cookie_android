package com.nuecoo.ui.fragment

import android.view.View
import android.view.animation.DecelerateInterpolator
import androidx.fragment.app.viewModels
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.GridLayoutManager
import com.nuecoo.core.ui.BaseFragment
import com.nuecoo.databinding.FragmentOvenBinding
import com.nuecoo.domain.CookieItemData
import com.nuecoo.mapper.toUiItem
import com.nuecoo.ui.adapter.CookieAdapter
import com.nuecoo.viewmodel.OvenFragmentViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OvenFragment : BaseFragment<FragmentOvenBinding>(FragmentOvenBinding::inflate) {
    private val viewModel: OvenFragmentViewModel by viewModels()

    private var cookieAdapter: CookieAdapter? = null

    override fun setUi() {
        binding.viewModel = viewModel
        bindingOnClick()
        setAdapter()
        binding.flTray.post {
            slideDown(binding.flTray)
        }
    }

    override fun setUpDate() {
    }

    override fun setObserve(lifecycleOwner: LifecycleOwner) {
        viewModel."".observe(lifecycleOwner, ::updateList)
    }

    private fun bindingOnClick() {
    }

    private fun setAdapter() {
        binding.rvCookie.run {
            layoutManager = GridLayoutManager(requireContext(), 2)
            cookieAdapter = CookieAdapter {

            }
            adapter = cookieAdapter
        }
    }

    private fun slideDown(view: View) {
        view.translationY = -view.height.toFloat()
        view.visibility = View.VISIBLE

        view.animate()
            .translationY(0f)
            .setDuration(600)
            .setInterpolator(DecelerateInterpolator())
            .start()
    }

    private fun updateList(items: List<CookieItemData>) {
        val uiItems = items.map { it.toUiItem() }
        cookieAdapter?.submitList(uiItems)
    }
}