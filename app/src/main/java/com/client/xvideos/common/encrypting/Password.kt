package com.client.xvideos.common.encrypting

import android.content.Context
import android.content.SharedPreferences
import android.util.Base64
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import androidx.core.content.edit
import timber.log.Timber
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import kotlin.experimental.xor

/**
 * У тебя один и тот же пароль даёт один и тот же AES-ключ, даже после переустановки.
 *
 * Пока приложение стоит — пароль хранится в зашифрованном SharedPreferences, удобно.
 *
 * После удаления приложение “забывает” пароль, и его нужно ввести снова.
 */
object Password {



    private const val KEY_ALIAS = "UserPasswordKey"
    private const val ANDROID_KEYSTORE = "AndroidKeyStore"
    private const val TRANSFORMATION = "AES/GCM/NoPadding"
    private const val GCM_IV_LENGTH = 12
    private const val GCM_TAG_LENGTH = 16

    var password: String? = null
        private set

    var key: SecretKeySpec? = null

    private const val KEY_SIZE = 256

    val CIPHER_ALGORITHM = "AES/GCM/NoPadding"

    /**
     * Сохранить пароль в SharedPreferences
     */
    fun savePassword(context: Context, password: String) {
        val prefs = getSecurePrefs(context)
        prefs.edit { putString("user_password", password) }
        this.password = password
        key = keyFromPassword(Password.password!!)
        Timber.i("!!! savePassword() password:$password")
        Timber.i("!!! savePassword() key:${key?.encoded?.joinToString(separator = "") { "%02X ".format(it) }}")
    }

    fun loadPassword(context: Context) {
        val prefs = getSecurePrefs(context)
        password = prefs.getString("user_password", null)
        Timber.i("!!! loadPassword() password:$password")
        if (password != null) {
            key = keyFromPassword(password!!)
            Timber.i("!!! loadPassword() key:${key?.encoded?.joinToString(separator = "") { "%02X ".format(it) }}")
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