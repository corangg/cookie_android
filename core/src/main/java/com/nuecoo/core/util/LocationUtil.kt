package com.nuecoo.core.util

import android.annotation.SuppressLint
import android.content.Context
import android.os.Looper
import kotlin.Pair
import com.google.android.gms.location.LocationAvailability
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


@SuppressLint("MissingPermission")
suspend fun getLocation(context: Context): Pair<Double, Double>? {

    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 0)
        .setWaitForAccurateLocation(true)
        .setMaxUpdateAgeMillis(0)
        .build()

    return suspendCoroutine { continuation ->
        var isResumed = false

        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    if (!isResumed) {
                        isResumed = true
                        fusedLocationClient.removeLocationUpdates(this)
                        val location = locationResult.lastLocation
                        if (location != null) {
                            continuation.resume(
                                Pair(
                                    location.latitude,
                                    location.longitude
                                )
                            )
                        } else {
                            continuation.resume(null)
                        }
                    }
                }

                override fun onLocationAvailability(locationAvailability: LocationAvailability) {
                    if (!locationAvailability.isLocationAvailable && !isResumed) {
                        isResumed = true
                        continuation.resume(null)
                    }
                }
            },
            Looper.getMainLooper()
        )
    }
}
