package com.nuecoo.ui.fragment

import androidx.fragment.app.activityViewModels
import androidx.lifecycle.LifecycleOwner
import com.bumptech.glide.Glide
import com.nuecoo.core.ui.BaseFragment
import com.nuecoo.databinding.FragmentCookieOpenBinding
import com.nuecoo.domain.CookieUIItemData
import com.nuecoo.mapper.toOpenItem
import com.nuecoo.viewmodel.OvenFragmentViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CookieOpenFragment : BaseFragment<FragmentCookieOpenBinding>(FragmentCookieOpenBinding::inflate) {
    private val viewModel: OvenFragmentViewModel by activityViewModels()

    override fun setUi() {
        binding.viewModel = viewModel
        bindingOnClick()
    }

    override fun setUpDate() {
    }

    override fun setObserve(lifecycleOwner: LifecycleOwner) {
        viewModel.selectCookieType.observe(lifecycleOwner,::setCookieUI)
    }

    private fun bindingOnClick() {
        binding.ivClose.setOnClickListener{
            requireActivity().supportFragmentManager.popBackStack()
        }
    }

    private fun setCookieUI(data: CookieUIItemData){
        val imgRes = data.toOpenItem()
        Glide.with(requireContext()).load(imgRes).into(binding.ivOpenCookie)
    }

}