// 原始文件归档: Bduan/qiantaiyunxin.kt
class MonitorService : Service() {
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(NOTIFICATION_ID, buildNotification())
        // 启动连接、定位、屏幕监听...
        return START_STICKY
    }
    
    private fun buildNotification(): Notification {
        // 必须创建通知通道
        val channel = NotificationChannel(CHANNEL_ID, "监控服务", NotificationManager.IMPORTANCE_LOW)
        getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        return Notification.Builder(this, CHANNEL_ID)
            .setContentTitle("靶机监控运行中")
            .setSmallIcon(R.drawable.ic_notify)
            .build()
    }
}
// 原始文件归档
