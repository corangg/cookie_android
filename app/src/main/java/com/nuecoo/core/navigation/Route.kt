package com.nuecoo.core.navigation

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
        const val FIND_EMAIL = "login/find-email"
        const val FIND_PW    = "login/find-pw"
    }

    object SignUp {
        const val GRAPH = "signup"
        const val TERMS = "signup/terms"
        const val PHONE = "signup/phone"
        const val EMAIL = "signup/email"
        const val PW = "signup/pw"
        const val NICKNAME = "signup/nickname"
        const val INFO = "signup/info"
        const val BIRTH = "signup/birth"
        const val COMPLETE = "signup/complete"
    }
}