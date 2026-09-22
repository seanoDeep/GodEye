// 原始文件归档: Bduan/congshu.kt
class TcpClient(private val serverHost: String, private val serverPort: Int) {
    private var socket: Socket? = null
    private var output: OutputStream? = null
    private val gson = Gson()
    private val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding") // 需初始化密钥
    
    fun connect() {
        thread {
            try {
                socket = Socket(serverHost, serverPort)
                output = socket?.getOutputStream()
                // 启动心跳线程
                startHeartbeat()
                // 接收服务端命令（可选）
            } catch (e: Exception) {
                // 重连
                Handler(Looper.getMainLooper()).postDelayed({ connect() }, 5000)
            }
        }
    }
    
    fun sendReport(data: ReportData) {
        try {
            val json = gson.toJson(data)
            val encrypted = encrypt(json) // AES 加密
            output?.write(encrypted)
            output?.flush()
        } catch (e: Exception) {
            reconnect()
        }
    }
    
    private fun startHeartbeat() {
        // 每10秒发送心跳包
        Handler(Looper.getMainLooper()).postDelayed({
            sendReport(ReportData(type = "heartbeat", deviceId = deviceId))
        }, 10000)
    }
    
    private fun reconnect() {
        disconnect()
        Handler(Looper.getMainLooper()).postDelayed({ connect() }, 3000)
    }
}
// 原始文件归档
