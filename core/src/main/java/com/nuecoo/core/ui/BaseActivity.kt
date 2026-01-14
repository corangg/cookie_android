package com.nuecoo.core.ui

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.LifecycleOwner
import com.nuecoo.core.util.LocaleHelper

typealias ActivityInflate<T> = (LayoutInflater) -> T

abstract class BaseActivity<VDB : ViewDataBinding>(
    private val inflater: ActivityInflate<VDB>
) : AppCompatActivity() {

    private var _binding: VDB? = null

    protected val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = inflater(layoutInflater).apply {
            lifecycleOwner = this@BaseActivity
        }
        setContentView(binding.root)

        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS,
            WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS
        )
        window.setStatusBarColor(Color.TRANSPARENT)
        window.setNavigationBarColor(Color.TRANSPARENT)

        val rootView = findViewById<View>(android.R.id.content)
        ViewCompat.setOnApplyWindowInsetsListener(rootView) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.updatePadding(
                top = systemBars.top,
                bottom = systemBars.bottom
            )
            WindowInsetsCompat.CONSUMED
        }

        setUpUi()
        setUpObserver(this)
        setUpData()
    }

    override fun attachBaseContext(newBase: Context?) {
        if (newBase == null) return
        val localeAppliedContext = LocaleHelper.applyLocale(newBase)
        super.attachBaseContext(localeAppliedContext)
    }

    abstract fun setUpUi()
    abstract fun setUpObserver(lifecycleOwner: LifecycleOwner)
    abstract fun setUpData()
}