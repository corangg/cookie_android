package com.nuecoo.core.service

import android.app.Service
import android.content.Intent
import android.os.IBinder

abstract class BaseService : Service() {

    override fun onBind(intent: Intent?): IBinder? = null
    override fun onUnbind(intent: Intent?) = true
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int) = START_STICKY
}