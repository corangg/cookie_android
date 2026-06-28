package com.nuecoo.core.navigation

import android.net.Uri

object Route {
    const val SPLASH = "splash"
    const val MAIN = "main"
    const val APP_INFO = "app_info"
    const val APP_RATE = "app_rate"
    const val APP_CS = "app_cs"
    const val APP_PRIVACY = "app_privacy"
    const val APP_TERMS = "app_terms"

    object Login {
        const val GRAPH      = "login"
        const val HOME       = "login/home"
        const val KAKAO      = "login/kakao"
        const val EMAIL      = "login/email"
        const val FIND_EMAIL = "login/find_email"
        const val FIND_EMAIL_COMPLETE = "login/find_email_complete/{maskedEmail}"

        fun findEmailComplete(maskedEmail: String) = "login/find_email_complete/${Uri.encode(maskedEmail)}"
        const val FIND_PW_EMAIL = "login/find_pw_email"
        const val FIND_PW_PHONE = "login/find_pw_phone"
        const val FIND_PW_RESET = "login/find_pw_reset"
        const val FIND_PW_COMPLETE = "login/find_pw_complete"
    }

    object SignUp {
        const val GRAPH = "signup"
        const val TERMS = "signup/terms"
        const val PHONE = "signup/phone"
        const val EMAIL = "signup/email"
        const val PW = "signup/pw"
        const val NICKNAME = "signup/nickname"
        const val INFO = "signup/info"
        const val COMPLETE = "signup/complete"
    }
}