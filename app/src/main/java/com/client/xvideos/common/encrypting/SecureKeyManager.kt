package com.client.xvideos.common.encrypting

import timber.log.Timber
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec

object SecureKeyManager {

    init {
        System.loadLibrary("securekeys") // Загружаем нативную библиотеку
    }

    // JNI методы
    private external fun nativeStoreKey(keyAlias: String, keyData: ByteArray): Boolean
    private external fun nativeGetKey(keyAlias: String): ByteArray?
    private external fun nativeClearKey(keyAlias: String): Boolean
    private external fun nativeHasKey(keyAlias: String): Boolean

    private const val KEY_ALIAS = "main_encryption_key"

    /**
     * Сохраняет ключ в обфусцированном виде в нативной памяти
     */
    fun storeSecretKey(secretKey: SecretKey): Boolean {
        return try {
            val keyBytes = secretKey.encoded
            nativeStoreKey(KEY_ALIAS, keyBytes)
        } catch (e: Exception) {
            Timber.e(e, "Error storing key")
            false
        }
    }

    /**
     * Получает ключ из нативной памяти
     */
    fun getSecretKey(): SecretKey? {
        return try {
            val keyBytes = nativeGetKey(KEY_ALIAS)
            if (keyBytes != null) {
                SecretKeySpec(keyBytes, "AES")
            } else {
                null
            }
        } catch (e: Exception) {
            Timber.e(e, "!!! Error retrieving key")
            null
        }
    }

    /**
     * Проверяет наличие ключа
     */
    fun hasKey(): Boolean = nativeHasKey(KEY_ALIAS)

    /**
     * Очищает ключ из памяти
     */
    fun clearKey(): Boolean = nativeClearKey(KEY_ALIAS)
}