package com.godeye.config

object Config {
    // 16 字节 AES-128 密钥（示例），生产环境请安全存储并更换
    const val AES_KEY = "0123456789abcdef"
    const val DEFAULT_HOST = "127.0.0.1"
    const val DEFAULT_PORT = 9000
}
