package com.nuecoo.core.util

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat

fun Context.isGranted(permission: String) =
    ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED

fun Context.isDenied(permission: String) =
    ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED

fun Context.isGrantedAll(vararg permissions: String) = permissions.all(::isGranted)
fun Context.isDeniedAny(vararg permissions: String) = permissions.any(::isDenied)

fun hasLocationPermissions() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
    Manifest.permission.ACCESS_BACKGROUND_LOCATION else null

fun hasNotificationPermission() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
    Manifest.permission.POST_NOTIFICATIONS else null

fun getMediaPermission(): String =
    when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE -> // Android 14 (34) 이상
            Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> // Android 13 (33)
            Manifest.permission.READ_MEDIA_IMAGES
        else -> // Android 12 (32) 이하
            Manifest.permission.READ_EXTERNAL_STORAGE
    }

val blueToothRequiredPermissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
    arrayOf(
        Manifest.permission.BLUETOOTH_SCAN,
        Manifest.permission.BLUETOOTH_CONNECT,
        Manifest.permission.ACCESS_FINE_LOCATION
    )
} else {
    arrayOf(
        Manifest.permission.BLUETOOTH,
        Manifest.permission.BLUETOOTH_ADMIN,
        Manifest.permission.ACCESS_FINE_LOCATION
    )
}