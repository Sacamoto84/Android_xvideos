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
import kotlin.experimental.xor

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


    private const val KEY_SIZE = 256
    private const val IV_SIZE = 12 // рекомендовано для GCM
    private const val TAG_SIZE = 128 // 16 байт аутентификационного тега

    // ключ для XOR (произвольный, можно изменить)
    private const val xorKey: Byte = 0x5A

    // "AES/GCM/NoPadding" → Base64 → XOR → массив
    // Для примера я взял готовый массив, но ты можешь сгенерировать заново через prepareEncoded()
    private val obfuscated = byteArrayOf( 27, 31, 31, 14, 22, 118, 118, 21, 21, 3, 101, 9, 13, 3, 101, 7, 22, 22, 3, 21, 7, 15, 13, 1, 101, 23, 22, 14, 3, 7, 15, 13 )

    /**
     * Декодирование строки алгоритма (AES/GCM/NoPadding).
     */
    val CIPHER_ALGORITHM: String by lazy {
        // 1. снимаем XOR
        val decodedBase64 = obfuscated.map { (it xor xorKey) }.toByteArray()
        // 2. превращаем в строку (это будет Base64 от исходного текста)
        val base64Str = decodedBase64.toString(Charsets.UTF_8)
        // 3. Декодируем Base64 в исходный текст
        Base64.decode(base64Str, Base64.DEFAULT).toString(Charsets.UTF_8)
    }


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
        val spec = PBEKeySpec(password.toCharArray(), salt, 65_536, KEY_SIZE)
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