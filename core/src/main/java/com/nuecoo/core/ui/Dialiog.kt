package com.nuecoo.core.ui

import android.app.AlertDialog
import android.content.Context

fun Context.showSimpleDialog(
    title: String,
    message: String,
    positive: String,
    negative: String,
    job: () -> Unit
) {
    AlertDialog.Builder(this)
        .setTitle(title)
        .setMessage(message)
        .setPositiveButton(positive) { _, _ ->
            job()
        }
        .setNegativeButton(negative, null)
        .show()
}