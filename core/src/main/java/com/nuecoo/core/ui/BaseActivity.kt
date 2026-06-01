package com.nuecoo.core.ui

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import com.nuecoo.core.util.LocaleHelper

abstract class BaseActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun attachBaseContext(newBase: Context?) {
        if (newBase == null) return
        val localeAppliedContext = LocaleHelper.applyLocale(newBase)
        super.attachBaseContext(localeAppliedContext)
    }
}
