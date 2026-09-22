package com.godeye.location

import android.content.Context
import com.godeye.model.LocationData
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority

class LocationCollector(private val context: Context) {
    private val fusedClient = LocationServices.getFusedLocationProviderClient(context)

    fun startLocationUpdates(callback: (LocationData) -> Unit) {
        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000L)
            .setMinUpdateIntervalMillis(500L)
            .build()

        fusedClient.requestLocationUpdates(locationRequest, object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                result.lastLocation?.let {
                    callback(LocationData(it.latitude, it.longitude, it.accuracy))
                }
            }
        }, android.os.Looper.getMainLooper())
    }
}
