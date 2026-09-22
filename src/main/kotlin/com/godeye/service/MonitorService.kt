package com.godeye.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.content.IntentFilter
import android.os.IBinder
import com.godeye.location.LocationCollector
import com.godeye.network.TcpClient
import com.godeye.screen.ScreenStateReceiver
import com.godeye.model.ReportData
import android.content.Context

class MonitorService : Service() {
    companion object {
        const val CHANNEL_ID = "godeye_monitor"
        const val NOTIFICATION_ID = 1001
    }

    private var client: TcpClient? = null
    private var locationCollector: LocationCollector? = null
    private var receiver: ScreenStateReceiver? = null

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        createNotificationChannel()
        startForeground(NOTIFICATION_ID, buildNotification())

        // TODO: host/port/deviceId 从配置或 intent 中读取
        client = TcpClient("127.0.0.1", 9000, "device-0001")
        client?.connect()

        locationCollector = LocationCollector(this)
        locationCollector?.startLocationUpdates { loc ->
            val report = ReportData(type = "report", deviceId = "device-0001", lat = loc.lat, lng = loc.lng, accuracy = loc.accuracy, updateTime = System.currentTimeMillis())
            client?.sendReport(report)
        }

        receiver = ScreenStateReceiver { screenOn, locked ->
            // 可把屏幕状态封装为上报字段或本地逻辑
        }
        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_SCREEN_ON)
            addAction(Intent.ACTION_SCREEN_OFF)
            addAction(Intent.ACTION_USER_PRESENT)
        }
        registerReceiver(receiver, filter)

        return START_STICKY
    }

    private fun createNotificationChannel() {
        val mgr = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channel = NotificationChannel(CHANNEL_ID, "监控服务", NotificationManager.IMPORTANCE_LOW)
        mgr.createNotificationChannel(channel)
    }

    private fun buildNotification(): Notification {
        return Notification.Builder(this, CHANNEL_ID)
            .setContentTitle("Godeye 监控服务")
            .setSmallIcon(android.R.drawable.ic_menu_info_details)
            .build()
    }

    override fun onDestroy() {
        super.onDestroy()
        try { client?.disconnect() } catch (_: Exception) {}
        try { unregisterReceiver(receiver) } catch (_: Exception) {}
    }
}
