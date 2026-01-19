package com.nuecoo.ui.fragment

import androidx.fragment.app.activityViewModels
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.nuecoo.controller.CookiePinchOpenController
import com.nuecoo.core.ui.BaseFragment
import com.nuecoo.databinding.FragmentCookieOpenBinding
import com.nuecoo.domain.CookieUIItemData
import com.nuecoo.mapper.toOpenAnimationItem
import com.nuecoo.mapper.toOpenItem
import com.nuecoo.viewmodel.OvenFragmentViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CookieOpenFragment : BaseFragment<FragmentCookieOpenBinding>(FragmentCookieOpenBinding::inflate) {
    private val viewModel: OvenFragmentViewModel by activityViewModels()

    lateinit var pinchController: CookiePinchOpenController

    private var openAnimJob: Job? = null

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
        if(!data.isOpened){
            setCookieOpenController(data)
        }
    }

    private fun setCookieOpenController(data: CookieUIItemData){
        pinchController = CookiePinchOpenController(
            targetView = binding.ivOpenCookie,
            onOpen = {
                viewModel.updateOpenCookieData(data.type)
                showCookieOpenAnimation(data)
            }
        )
    }

    private fun showCookieOpenAnimation(data: CookieUIItemData){
        openAnimJob?.cancel()

        val imgList = data.toOpenAnimationItem()

        openAnimJob = viewLifecycleOwner.lifecycleScope.launch {
            for (resId in imgList) {
                binding.ivOpenCookie.setImageResource(resId)
                delay(800L)
            }
        }
    }
}