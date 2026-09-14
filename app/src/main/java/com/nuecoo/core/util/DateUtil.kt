package com.nuecoo.core.util

import android.content.Context
import com.nuecoo.R

fun String.toDisplayDate(): String = "${substring(0, 4)}.${substring(4, 6)}.${substring(6, 8)}"

fun String.toDateUnit(context: Context): String =
    "${take(4)}${context.getString(R.string.year)} " +
            "${drop(4).take(2)}${context.getString(R.string.month)} " +
            "${drop(6).take(2)}${context.getString(R.string.day)}"