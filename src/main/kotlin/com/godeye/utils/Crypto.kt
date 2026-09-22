package com.godeye.utils

import android.util.Base64
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

object Crypto {
    private const val TRANSFORMATION = "AES/CBC/PKCS5Padding"
    private const val ALGORITHM = "AES"
    private const val IV_SIZE = 16

    fun encryptToBase64(plain: String, key: String): String {
        val keySpec = SecretKeySpec(key.toByteArray(Charsets.UTF_8), ALGORITHM)
        val iv = ByteArray(IV_SIZE)
        SecureRandom().nextBytes(iv)
        val ivSpec = IvParameterSpec(iv)
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec)
        val encrypted = cipher.doFinal(plain.toByteArray(Charsets.UTF_8))
        val out = ByteArray(iv.size + encrypted.size)
        System.arraycopy(iv, 0, out, 0, iv.size)
        System.arraycopy(encrypted, 0, out, iv.size, encrypted.size)
        return Base64.encodeToString(out, Base64.NO_WRAP)
    }

    fun decryptFromBase64(base64: String, key: String): String {
        val all = Base64.decode(base64, Base64.NO_WRAP)
        if (all.size <= IV_SIZE) return ""
        val iv = all.copyOfRange(0, IV_SIZE)
        val cipherBytes = all.copyOfRange(IV_SIZE, all.size)
        val keySpec = SecretKeySpec(key.toByteArray(Charsets.UTF_8), ALGORITHM)
        val ivSpec = IvParameterSpec(iv)
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec)
        val decrypted = cipher.doFinal(cipherBytes)
        return String(decrypted, Charsets.UTF_8)
    }
}
