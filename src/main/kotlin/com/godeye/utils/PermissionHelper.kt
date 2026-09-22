package com.godeye.utils

import android.app.Activity
import androidx.core.app.ActivityCompat
import android.Manifest

object PermissionHelper {
    private val REQUIRED = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.FOREGROUND_SERVICE
    )

    fun requestPermissions(activity: Activity, requestCode: Int) {
        ActivityCompat.requestPermissions(activity, REQUIRED, requestCode)
    }
}
