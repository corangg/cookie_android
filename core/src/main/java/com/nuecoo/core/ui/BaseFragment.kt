package com.nuecoo.core.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner

typealias FragmentInflate<T> = (LayoutInflater, ViewGroup?, Boolean) -> T

abstract class BaseFragment<VDB : ViewDataBinding>(
    private val inflater: FragmentInflate<VDB>
) : Fragment() {
    protected lateinit var binding: VDB

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = inflater(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = viewLifecycleOwner

        setUi()
        setUpDate()
        setObserve(viewLifecycleOwner)
    }

    abstract fun setUi()
    abstract fun setUpDate()
    abstract fun setObserve(lifecycleOwner: LifecycleOwner)
}