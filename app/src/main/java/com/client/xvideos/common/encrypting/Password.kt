package com.client.xvideos.common.encrypting

import android.content.Context
import android.content.SharedPreferences
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import androidx.core.content.edit

/**
 * У тебя один и тот же пароль даёт один и тот же AES-ключ, даже после переустановки.
 *
 * Пока приложение стоит — пароль хранится в зашифрованном SharedPreferences, удобно.
 *
 * После удаления приложение “забывает” пароль, и его нужно ввести снова.
 */
object Password {

    var password: String? = null

    var key: SecretKeySpec? = null

    val CIPHER_ALGORITHM: String = "AES/CBC/PKCS5Padding"


    fun savePassword(context: Context, password: String) {
        val prefs = getSecurePrefs(context)
        prefs.edit { putString("user_password", password) }
    }

    fun loadPassword(context: Context): String? {
        val prefs = getSecurePrefs(context)
        return prefs.getString("user_password", null)
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
    fun keyFromPassword(password: String, salt: ByteArray): SecretKeySpec {
        val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
        val spec = PBEKeySpec(password.toCharArray(), salt, 65_536, 256)
        val tmp = factory.generateSecret(spec)
        return SecretKeySpec(tmp.encoded, CIPHER_ALGORITHM)
    }


    fun initKey(context: Context, salt: ByteArray): SecretKeySpec {
        val stored = loadPassword(context)

        val password = if (stored == null) {
            // здесь показываешь экран "Введите пароль"
            val userInput = "askUserPassword()"
            savePassword(context, userInput)
            userInput
        } else {
            stored
        }

        return keyFromPassword(password, salt)
    }

    private fun getSecurePrefs(context: Context): SharedPreferences {
        val masterKey =
            MasterKey.Builder(context).setKeyScheme(MasterKey.KeyScheme.AES256_GCM).build()

        return EncryptedSharedPreferences.create(
            context,
            "secure_prefs", // имя файла
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

}