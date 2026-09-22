// 原始文件归档: Aduan/Azhuyao7.kt
class TcpServer(private val port: Int) {
    private val serverSocket = ServerSocket(port)
    private val clients = ConcurrentHashMap<String, ClientHandler>()
    
    fun start() {
        thread {
            while (true) {
                val socket = serverSocket.accept()
                val deviceId = "unknown" // 首包认证获取
                val handler = ClientHandler(socket, deviceId)
                clients[deviceId] = handler
                handler.start()
            }
        }
    }
    
    inner class ClientHandler(private val socket: Socket, private var deviceId: String) {
        fun start() {
            thread {
                val input = socket.getInputStream()
                val buffer = ByteArray(4096)
                while (true) {
                    val len = input.read(buffer)
                    if (len == -1) break
                    val encrypted = buffer.copyOf(len)
                    val json = decrypt(encrypted) // AES解密
                    val report = Gson().fromJson(json, ReportData::class.java)
                    if (report.type == "report" || report.type == "heartbeat") {
                        updateUI(report)
                    }
                }
                clients.remove(deviceId)
            }
        }
    }
}
// 原始文件归档
class TcpServerOriginal {
    // ...
}
