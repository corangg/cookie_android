package com.nuecoo.ui.fragment

import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.map
import androidx.recyclerview.widget.GridLayoutManager
import com.nuecoo.R
import com.nuecoo.core.ui.BaseFragment
import com.nuecoo.databinding.FragmentOvenBinding
import com.nuecoo.domain.model.CookieItemData
import com.nuecoo.domain.model.CookieUIItemData
import com.nuecoo.mapper.toUiItem
import com.nuecoo.ui.adapter.CookieAdapter
import com.nuecoo.ui.util.CustomToast
import com.nuecoo.viewmodel.OvenFragmentViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OvenFragment : BaseFragment<FragmentOvenBinding>(FragmentOvenBinding::inflate) {
    private val viewModel: OvenFragmentViewModel by activityViewModels()

    private var cookieAdapter: CookieAdapter? = null

    override fun setUi() {
        binding.viewModel = viewModel
        bindingOnClick()
        setAdapter()
        binding.flTray.post {
            slideDown(binding.flTray)
        }
        initCookieData()
    }

    override fun setUpDate() {
    }

    override fun setObserve(lifecycleOwner: LifecycleOwner) {
        viewModel.dailyCookieData.map { it.list }.observe(lifecycleOwner, ::updateList)
    }

    private fun bindingOnClick() {
    }

    private fun setAdapter() {
        binding.rvCookie.run {
            layoutManager = GridLayoutManager(requireContext(), 2)
            cookieAdapter = CookieAdapter {
                if(it.isOpened == null){
                    CustomToast.createToast(requireContext(), getString(R.string.text_toast_cookie_all_collect)).show()

                }else{
                    showCookieOpenDialog(it)
                }
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

    private fun showCookieOpenDialog(data: CookieUIItemData) {
        requireActivity().supportFragmentManager.beginTransaction()
            .setReorderingAllowed(true)
            .add(R.id.overlay_container, CookieOpenFragment(), "CookieOpen")
            .addToBackStack("overlay")
            .commit()
        viewModel.setSelectCookieType(data)
    }

    private fun initCookieData() {
        val names = mapOf(
            0 to resources.getStringArray(R.array.cookie_type_cheering).toList(),
            1 to resources.getStringArray(R.array.cookie_type_consolation).toList(),
            2 to resources.getStringArray(R.array.cookie_type_passion).toList(),
            3 to resources.getStringArray(R.array.cookie_type_determination).toList(),
        )

        viewModel.getDailyCookieData(names)
    }
}