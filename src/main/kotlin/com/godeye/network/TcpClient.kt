package com.godeye.network

import com.godeye.model.ReportData
import com.godeye.config.Config
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec
import com.google.gson.Gson
import kotlin.concurrent.thread
import java.io.OutputStream
import java.net.Socket
import java.util.concurrent.ScheduledThreadPoolExecutor
import java.util.concurrent.TimeUnit

class TcpClient(
    private val serverHost: String,
    private val serverPort: Int,
    private val deviceId: String
) {
    private var socket: Socket? = null
            val payload = Crypto.encryptToBase64(json, Config.AES_KEY)
            val bytes = payload.toByteArray(Charsets.UTF_8)
            // 发送长度前缀（4 字节 big-endian） + body
            val len = bytes.size
            val header = byteArrayOf(
                ((len shr 24) and 0xFF).toByte(),
                ((len shr 16) and 0xFF).toByte(),
                ((len shr 8) and 0xFF).toByte(),
                (len and 0xFF).toByte()
            )
            output?.write(header)
            output?.write(bytes)
            output?.flush()

    fun connect() {
        thread {
            try {
                socket = Socket(serverHost, serverPort)
                output = socket?.getOutputStream()
                startHeartbeat()
            } catch (e: Exception) {
                reconnect()
            }
        }
    }

    fun sendReport(data: ReportData) {
        try {
            val json = gson.toJson(data)
            val encrypted = encrypt(json)
            output?.write(encrypted)
            output?.write('\n'.code)
            output?.flush()
        } catch (e: Exception) {
            reconnect()
        }
    }

    private fun startHeartbeat() {
        scheduler.scheduleAtFixedRate({
            val hb = ReportData(type = "heartbeat", deviceId = deviceId, updateTime = System.currentTimeMillis())
            sendReport(hb)
        }, 10, 10, TimeUnit.SECONDS)
    }

    private fun encrypt(plain: String): ByteArray {
        // encryption delegated to Crypto; keep method for compatibility
        return Crypto.encryptToBase64(plain, Config.AES_KEY).toByteArray(Charsets.UTF_8)

    private fun reconnect() {
        disconnect()
        scheduler.schedule({ connect() }, 3, TimeUnit.SECONDS)
    }

    fun disconnect() {
        try { socket?.close() } catch (_: Exception) {}
        socket = null
    }
}
