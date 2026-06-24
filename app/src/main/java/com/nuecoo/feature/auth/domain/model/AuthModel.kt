package com.nuecoo.feature.auth.domain.model

sealed class LoginResult {
    object Success : LoginResult()
    object Empty : LoginResult()
    object Failed : LoginResult()
}

sealed class EmailCheckResult {
    object Available : EmailCheckResult()
    object Duplicated : EmailCheckResult()
    object Error : EmailCheckResult()
}

sealed class PwCheckResult {
    object Success : PwCheckResult()
    object NotAccordance : PwCheckResult()
    object NotValid : PwCheckResult()
}

data class CollectionDisplayItem(
    val no: Int,
    val isCollected: Boolean,
    val type: Int,
    val date: String? = null
)
