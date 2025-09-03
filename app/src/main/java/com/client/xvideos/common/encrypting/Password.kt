package com.client.xvideos.common.encrypting

import android.content.Context
import android.content.SharedPreferences
import android.util.Base64
import androidx.core.content.edit
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import timber.log.Timber
import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec

/**
 * У тебя один и тот же пароль даёт один и тот же AES-ключ, даже после переустановки.
 *
 * Пока приложение стоит — пароль хранится в зашифрованном SharedPreferences, удобно.
 *
 * После удаления приложение “забывает” пароль, и его нужно ввести снова.
 */
object Password {

    var key: SecretKeySpec? = null
        private set

    private const val KEY_SIZE = 256

    val CIPHER_ALGORITHM = "AES/GCM/NoPadding"

    private val userPassword = "user_password"

    private const val GCM_IV_LENGTH = 12
    private const val GCM_TAG_LENGTH = 16
    /**
     * Сохранить пароль в SharedPreferences
     */
    fun savePassword(context: Context, password: String) {
        val prefs = getSecurePrefs(context)
        prefs.edit { putString(userPassword, password) }
        key = keyFromPassword(password)
        Timber.i("!!! savePassword() password:$password")
        Timber.i("!!! savePassword() key:${key?.encoded?.joinToString(separator = ""){"%02X ".format(it)}}")
    }

    fun loadPassword(context: Context) {
        val prefs = getSecurePrefs(context)
        val password = prefs.getString(userPassword, null)
        Timber.i("!!! loadPassword() password:$password")
        if (password != null) {
            key = keyFromPassword(password)
            Timber.i("!!! loadPassword() key:${key?.encoded?.joinToString(separator = ""){"%02X ".format(it)}}")
        }
    }

    /**
     * Создаёт фабрику для алгоритма PBKDF2 с HMAC-SHA256.
     * Обычный пароль слишком слабый для AES, PBKDF2 делает его безопасным ключом нужной длины.
     *
     * - password.toCharArray() — сам пароль.
     * - salt — случайная последовательность байт (обеспечивает уникальность ключа даже при одинаковом пароле). val salt1 = ByteArray(16) { 0x01 }
     * - 65536 — количество итераций PBKDF2. Чем больше, тем сложнее подобрать ключ подбором.
     * - 256 — длина ключа в битах (AES-256).
     */
    fun keyFromPassword(password: String, salt: ByteArray = ByteArray(16) { 0x01 }): SecretKeySpec {
        val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
        val spec = PBEKeySpec(password.toCharArray(), salt, 65_536, KEY_SIZE)
        val tmp = factory.generateSecret(spec)
        return SecretKeySpec(tmp.encoded, CIPHER_ALGORITHM)
    }

    private fun getSecurePrefs(context: Context): SharedPreferences {
        val masterKey = MasterKey.Builder(context).setKeyScheme(MasterKey.KeyScheme.AES256_GCM).setRequestStrongBoxBacked(true).build()

        return EncryptedSharedPreferences.create(
            context,
            "secure_prefs", // имя файла
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    /**
     * Проверяет, доступен ли ключ для шифрования
     */
    fun isKeyAvailable(): Boolean {
        return key != null
    }

    /**
     * Очищает пароль и ключ
     */
    fun clearPassword(context: Context) {
        try {
            val prefs = getSecurePrefs(context)
            prefs.edit { remove(userPassword) }
            key = null
            Timber.i("!!! Password and key cleared successfully")
        } catch (e: Exception) {
            Timber.e(e, "!!! eee Error clearing password")
        }
    }

    /**
     * Проверяет, сохранён ли пароль
     */
    fun isPasswordStored(context: Context): Boolean {
        return try {
            val prefs = getSecurePrefs(context)
            prefs.contains(userPassword)
        } catch (e: Exception) {
            Timber.e(e, "!!! eee Error checking stored password")
            false
        }
    }

    /**
     * Шифрует данные с помощью текущего ключа
     */
    fun encrypt(data: String): String? {
        return try {
            val currentKey = key ?: throw IllegalStateException("Key not available. Call savePassword() or loadPassword() first")

            val cipher = Cipher.getInstance(CIPHER_ALGORITHM)
            cipher.init(Cipher.ENCRYPT_MODE, currentKey)

            val iv = cipher.iv
            val encryptedData = cipher.doFinal(data.toByteArray(Charsets.UTF_8))

            // Объединяем IV и зашифрованные данные
            val combined = iv + encryptedData
            Base64.encodeToString(combined, Base64.DEFAULT)
        } catch (e: Exception) {
            Timber.e(e, "!!! eee Error encrypting data")
            null
        }
    }

    /**
     * Расшифровывает данные с помощью текущего ключа
     */
    fun decrypt(encryptedData: String): String? {
        return try {
            val currentKey = key ?: throw IllegalStateException("Key not available. Call savePassword() or loadPassword() first")

            val combined = Base64.decode(encryptedData, Base64.DEFAULT)

            // Извлекаем IV и зашифрованные данные
            val iv = combined.sliceArray(0 until GCM_IV_LENGTH)
            val cipherText = combined.sliceArray(GCM_IV_LENGTH until combined.size)

            val cipher = Cipher.getInstance(CIPHER_ALGORITHM)
            val spec = GCMParameterSpec(GCM_TAG_LENGTH * 8, iv)
            cipher.init(Cipher.DECRYPT_MODE, currentKey, spec)

            val decryptedData = cipher.doFinal(cipherText)
            String(decryptedData, Charsets.UTF_8)
        } catch (e: Exception) {
            Timber.e(e, "!!! eee Error decrypting data")
            null
        }
    }
}