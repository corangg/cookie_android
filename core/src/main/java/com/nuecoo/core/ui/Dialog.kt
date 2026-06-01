package com.nuecoo.core.ui

import android.content.Context
import androidx.appcompat.app.AlertDialog

fun Context.showSimpleDialog(
    title: String,
    message: String,
    positiveText: String,
    negativeText: String,
    onPositiveClick: () -> Unit = {}
) {
    AlertDialog.Builder(this)
        .setTitle(title)
        .setMessage(message)
        .setPositiveButton(positiveText) { _, _ -> onPositiveClick() }
        .setNegativeButton(negativeText) { dialog, _ -> dialog.dismiss() }
        .show()
}
