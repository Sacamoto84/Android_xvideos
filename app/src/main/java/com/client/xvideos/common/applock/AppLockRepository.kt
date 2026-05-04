package com.client.xvideos.common.applock

import android.content.Context
import android.preference.PreferenceManager
import android.util.Base64
import androidx.core.content.edit
import com.client.xvideos.common.settings.Settings
import java.security.MessageDigest
import java.security.SecureRandom
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

object AppLockRepository {

    private const val KEY_PASSWORD_HASH = "app_lock_password_hash"
    private const val KEY_PASSWORD_SALT = "app_lock_password_salt"
    private const val PBKDF2_ALGORITHM = "PBKDF2WithHmacSHA256"
    private const val HASH_BITS = 256
    private const val ITERATIONS = 120_000
    private const val SALT_BYTES = 16
    private const val MIN_PASSWORD_LENGTH = 4

    fun isPasswordSet(context: Context): Boolean {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context.applicationContext)
        return !prefs.getString(KEY_PASSWORD_HASH, null).isNullOrBlank() &&
                !prefs.getString(KEY_PASSWORD_SALT, null).isNullOrBlank()
    }

    fun isEnabled(context: Context): Boolean {
        return Settings.app_lock_enabled.field.value && isPasswordSet(context)
    }

    fun shouldShowLock(context: Context): Boolean {
        return isEnabled(context) && !AppLockSession.isUnlocked()
    }

    fun setPassword(context: Context, password: String): Result<Unit> = runCatching {
        require(password.length >= MIN_PASSWORD_LENGTH) {
            "Код доступа должен быть не короче $MIN_PASSWORD_LENGTH символов"
        }

        val salt = ByteArray(SALT_BYTES).also { SecureRandom().nextBytes(it) }
        val hash = hashPassword(password, salt)
        val prefs = PreferenceManager.getDefaultSharedPreferences(context.applicationContext)

        prefs.edit {
            putString(KEY_PASSWORD_SALT, salt.toBase64())
            putString(KEY_PASSWORD_HASH, hash.toBase64())
        }
        Settings.app_lock_enabled.setValue(true)
        AppLockSession.unlock()
    }

    fun verifyPassword(context: Context, password: String): Boolean {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context.applicationContext)
        val salt = prefs.getString(KEY_PASSWORD_SALT, null)?.fromBase64() ?: return false
        val expectedHash = prefs.getString(KEY_PASSWORD_HASH, null)?.fromBase64() ?: return false
        val actualHash = hashPassword(password, salt)
        return MessageDigest.isEqual(expectedHash, actualHash)
    }

    fun clearPassword(context: Context) {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context.applicationContext)
        prefs.edit {
            remove(KEY_PASSWORD_SALT)
            remove(KEY_PASSWORD_HASH)
        }
        Settings.app_lock_enabled.setValue(false)
        AppLockSession.lock()
    }

    private fun hashPassword(password: String, salt: ByteArray): ByteArray {
        val spec = PBEKeySpec(password.toCharArray(), salt, ITERATIONS, HASH_BITS)
        return SecretKeyFactory.getInstance(PBKDF2_ALGORITHM).generateSecret(spec).encoded
    }

    private fun ByteArray.toBase64(): String {
        return Base64.encodeToString(this, Base64.NO_WRAP)
    }

    private fun String.fromBase64(): ByteArray {
        return Base64.decode(this, Base64.NO_WRAP)
    }
}
