package com.gitgrass.global.security.crypto

import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec
import java.util.Base64

object AesUtil {
    private const val ALGORITHM = "AES/CBC/PKCS5Padding"
    // 실제 운영 시 외부 환경 변수를 주입받도록 구성하는 것을 권장합니다.
    private val key = SecretKeySpec("01234567890123456789012345678901".toByteArray(), "AES") // 32 bytes for AES-256
    private val iv = IvParameterSpec("0123456789abcdef".toByteArray()) // 16 bytes IV

    fun encrypt(plainText: String): String {
        val cipher = Cipher.getInstance(ALGORITHM)
        cipher.init(Cipher.ENCRYPT_MODE, key, iv)
        val encrypted = cipher.doFinal(plainText.toByteArray())
        return Base64.getEncoder().encodeToString(encrypted)
    }

    fun decrypt(cipherText: String): String {
        val cipher = Cipher.getInstance(ALGORITHM)
        cipher.init(Cipher.DECRYPT_MODE, key, iv)
        val decoded = Base64.getDecoder().decode(cipherText)
        val decrypted = cipher.doFinal(decoded)
        return String(decrypted)
    }
}
