package com.nuecoo.core.util

import android.Manifest
import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.os.Build
import androidx.annotation.DrawableRes
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

private fun Context.getNotificationManager() = getSystemService(NotificationManager::class.java)

private fun Context.getNotificationManagerCompat() = NotificationManagerCompat.from(this)

fun Context.createNotificationChannel(
    channelId: String,
    channelName: String,
    importance: Int = NotificationManager.IMPORTANCE_LOW
) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channel = NotificationChannel(channelId, channelName, importance)
        getNotificationManager().createNotificationChannel(channel)
    }
}

@Suppress("DEPRECATION")
fun Context.createNotificationBuilder(
    channelId: String,
    @DrawableRes iconSmall: Int,
    ongoing: Boolean = true
): NotificationCompat.Builder {
    val builder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        NotificationCompat.Builder(this, channelId)
    } else {
        NotificationCompat.Builder(this)
    }

    val intent = packageManager.getLaunchIntentForPackage(packageName)
    val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)

    return builder
        .setForegroundServiceBehavior(NotificationCompat.FOREGROUND_SERVICE_IMMEDIATE)
        .setSmallIcon(iconSmall)
        .setContentIntent(pendingIntent).setAutoCancel(false)
        .setOngoing(ongoing)
}

@SuppressLint("MissingPermission")
fun Context.showNotification(
    notificationId: Int,
    notification: Notification
) {
    if (isDenied(Manifest.permission.POST_NOTIFICATIONS)) return

    getNotificationManagerCompat().notify(notificationId, notification)
}