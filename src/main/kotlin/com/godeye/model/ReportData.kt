package com.godeye.model

import com.google.gson.annotations.SerializedName

data class ReportData(
    @SerializedName("type") val type: String = "report",
    @SerializedName("device_id") val deviceId: String = "",
    @SerializedName("lat") val lat: Double = 0.0,
    @SerializedName("lng") val lng: Double = 0.0,
    @SerializedName("accuracy") val accuracy: Float? = null,
    @SerializedName("screen_state") val screenState: Int = 0,
    @SerializedName("lock_state") val lockState: Int = 0,
    @SerializedName("update_time") val updateTime: Long = System.currentTimeMillis(),
    @SerializedName("battery") val battery: Int? = null,
    @SerializedName("seq") val seq: Long? = null
)
