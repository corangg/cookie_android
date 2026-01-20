package com.nuecoo.ui.fragment

import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.nuecoo.R
import com.nuecoo.controller.CookiePinchOpenController
import com.nuecoo.core.ui.BaseFragment
import com.nuecoo.databinding.FragmentCookieOpenBinding
import com.nuecoo.domain.model.CookieUIItemData
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

    private val cookieList by lazy {
        mapOf(
            0 to resources.getStringArray(R.array.cookie_type_cheering).toList(),
            1 to resources.getStringArray(R.array.cookie_type_consolation).toList(),
            2 to resources.getStringArray(R.array.cookie_type_passion).toList(),
            3 to resources.getStringArray(R.array.cookie_type_determination).toList(),
        )
    }

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
        data.isOpened?.let {
            if(!it){
                setCookieOpenController(data)
            }else{
                setCookieMessage(data.type)
            }
        }
    }

    private fun setCookieOpenController(data: CookieUIItemData){
        pinchController = CookiePinchOpenController(
            targetView = binding.ivOpenCookie,
            onOpen = {
                viewModel.updateOpenCookieData(data.type, cookieList)
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
            if (::pinchController.isInitialized) pinchController.dispose()
            setCookieMessage(data.type)
        }
    }

    private fun setCookieMessage(type: Int) {
        val dailyCookieData = viewModel.dailyCookieData.value
        dailyCookieData?.let { data ->
            val cookieNo = data.list.find { it.type == type }?.no ?: return
            val message = cookieList[type]?.getOrNull(cookieNo - 1) ?: getString(R.string.text_message_open_cookie_error)
            binding.groupMessage.visibility = View.VISIBLE
            binding.tvMessage.text = message
        }
    }

    override fun onDestroyView() {
        openAnimJob?.cancel()
        if (::pinchController.isInitialized) pinchController.dispose()
        super.onDestroyView()
    }
}