package com.godeye.network

import com.godeye.model.ReportData
import com.godeye.config.Config
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec
import com.google.gson.Gson
import com.godeye.utils.Crypto
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.Closeable
import java.net.ServerSocket
import java.util.concurrent.ConcurrentHashMap
import kotlin.concurrent.thread

class TcpServer(private val port: Int, private val onReport: (ReportData) -> Unit) : Closeable {
    private val serverSocket = ServerSocket(port)
    private val clients = ConcurrentHashMap<String, ClientHandler>()
    @Volatile private var running = false
    private val gson = Gson()

    fun start() {
        running = true
        thread {
            while (running) {
                try {
                    val socket = serverSocket.accept()
                    val handler = ClientHandler(socket)
                    handler.start()
                } catch (e: Exception) {
                    // accept 失败或已关闭
                }
            }
        }
    }

    inner class ClientHandler(private val socket: java.net.Socket) {
        private val reader = BufferedReader(InputStreamReader(socket.getInputStream()))
        private var deviceId: String = "unknown"

        fun start() {
            thread {
                try {
                    val input = socket.getInputStream()
                    val header = ByteArray(4)
                    while (true) {
                        // 读取 4 字节长度前缀
                        var read = input.readNBytes(header, 0, 4)
                        if (read < 4) break
                        val len = ((header[0].toInt() and 0xFF) shl 24) or
                                  ((header[1].toInt() and 0xFF) shl 16) or
                                  ((header[2].toInt() and 0xFF) shl 8) or
                                  (header[3].toInt() and 0xFF)
                        if (len <= 0) continue
                        val body = ByteArray(len)
                        var pos = 0
                        while (pos < len) {
                            val n = input.read(body, pos, len - pos)
                            if (n <= 0) throw java.io.EOFException()
                            pos += n
                        }
                        val base64 = String(body, Charsets.UTF_8)
                        val json = try { Crypto.decryptFromBase64(base64, Config.AES_KEY) } catch (e: Exception) { base64 }
                        val report = try { gson.fromJson(json, ReportData::class.java) } catch (e: Exception) { null }
                        report?.let {
                            if (it.deviceId.isNotEmpty()) deviceId = it.deviceId
                            clients[deviceId] = this
                            onReport(it)
                        }
                    }
                } catch (e: Exception) {
                    // 连接异常
                } finally {
                    try { socket.close() } catch (_: Exception) {}
                    clients.remove(deviceId)
                }
            }
        }

        fun stop() {
            try { socket.close() } catch (_: Exception) {}
        }
    }

    private fun decrypt(bytes: ByteArray): String {
        val key = SecretKeySpec(Config.AES_KEY.toByteArray(Charsets.UTF_8), "AES")
        val iv = IvParameterSpec(ByteArray(16))
        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        cipher.init(Cipher.DECRYPT_MODE, key, iv)
        val decrypted = cipher.doFinal(bytes)
        return String(decrypted, Charsets.UTF_8)
    }

    override fun close() {
        running = false
        try { serverSocket.close() } catch (_: Exception) {}
        clients.values.forEach { it.stop() }
    }
}
