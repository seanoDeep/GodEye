package com.godeye.ui

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.amap.api.maps.AMap
import com.amap.api.maps.model.LatLng
import com.amap.api.maps.model.Marker
import com.amap.api.maps.model.MarkerOptions
import com.amap.api.maps.model.BitmapDescriptorFactory
import com.godeye.model.ReportData
import java.util.concurrent.ConcurrentHashMap

class MarkerManager(private val context: Context, private val aMap: AMap) {
    private val markers = ConcurrentHashMap<String, Marker?>()

    fun updateDeviceMarker(deviceId: String, data: ReportData) {
        val latLng = LatLng(data.lat, data.lng)
        val marker = markers[deviceId]
        if (marker == null) {
            val options = MarkerOptions()
                .position(latLng)
                .title(deviceId)
                .icon(BitmapDescriptorFactory.fromView(createMarkerIcon(data)))
            markers[deviceId] = aMap.addMarker(options)
        } else {
            marker.position = latLng
            marker.setIcon(BitmapDescriptorFactory.fromView(createMarkerIcon(data)))
        }
    }

    private fun createMarkerIcon(data: ReportData): View {
        val size = dpToPx(40)
        val circleSize = dpToPx(30)
        val layout = LinearLayout(context).apply {
            layoutParams = ViewGroup.LayoutParams(size, size)
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
        }

        val color = when {
            data.lockState == 1 -> Color.parseColor("#FF3B30")
            data.screenState == 1 -> Color.parseColor("#34C759")
            else -> Color.parseColor("#8E8E93")
        }

        val bg = GradientDrawable().apply {
            shape = GradientDrawable.OVAL
            setColor(color)
            setStroke(dpToPx(2), Color.WHITE)
        }

        val circle = TextView(context).apply {
            layoutParams = LinearLayout.LayoutParams(circleSize, circleSize)
            background = bg
            gravity = Gravity.CENTER
            setTextColor(Color.WHITE)
            textSize = 10f
            text = data.battery?.let { "$it%" } ?: ""
        }

        layout.addView(circle)
        return layout
    }

    private fun dpToPx(dp: Int): Int {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp.toFloat(), context.resources.displayMetrics).toInt()
    }
}
