package com.nuecoo.core.util

import android.content.Context
import android.content.res.Configuration
import java.util.Locale

object LocaleHelper {

    private const val PREF_NAME = "app_settings"
    private const val KEY_LANGUAGE = "language"
    private const val KEY_FIRST_RUN = "is_first_run"

    /** 언어코드 저장 */
    fun setLanguage(context: Context, languageCode: String) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_LANGUAGE, languageCode).apply()
    }

    /** 저장된 언어코드 불러오기 (최초 실행 시 영어로 1회만 설정) */
    fun getLanguage(context: Context): String {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

        val isFirstRun = prefs.getBoolean(KEY_FIRST_RUN, true)
        val savedLang = prefs.getString(KEY_LANGUAGE, null)

        return if (isFirstRun || savedLang == null) {
            // ✅ 앱 최초 실행 시 영어(en)로 강제 설정
            prefs.edit()
                .putString(KEY_LANGUAGE, "ko")
                .putBoolean(KEY_FIRST_RUN, false)
                .apply()
            "ko"
        } else {
            savedLang
        }
    }

    /** Locale 적용 */
    fun applyLocale(context: Context): Context {
        val language = getLanguage(context)
        val locale = Locale(language)
        Locale.setDefault(locale)

        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)

        return context.createConfigurationContext(config)
    }
}
