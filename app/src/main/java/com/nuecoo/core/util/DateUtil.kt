package com.nuecoo.core.util

fun String.toDisplayDate(): String = "${substring(0, 4)}.${substring(4, 6)}.${substring(6, 8)}"