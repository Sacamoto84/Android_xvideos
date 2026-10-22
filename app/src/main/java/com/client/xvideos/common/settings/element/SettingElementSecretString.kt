package com.client.xvideos.common.settings.element

import android.content.SharedPreferences
import androidx.core.content.edit
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import timber.log.Timber

/**
 * Строковая настройка для секретов (пароли). API как у [SettingElementString],
 * но в SharedPreferences значение лежит зашифрованным через [KeystoreCipher].
 *
 * @param legacyName ключ, под которым значение раньше хранилось открытым текстом:
 * при создании оно шифруется под [name], а старая запись удаляется.
 */
class SettingElementSecretString(
    private val sharedPrefs: SharedPreferences,
    val name: String,
    private val legacyName: String? = null,
    val default: String = ""
) {
    private val _field = MutableStateFlow(default)
    val field: StateFlow<String> = _field.asStateFlow()

    fun setValue(value: String) {
        if (value.isEmpty()) {
            sharedPrefs.edit { remove(name) }
        } else {
            val encrypted = runCatching { KeystoreCipher.encrypt(value) }
                .onFailure { Timber.e(it, "Не удалось зашифровать настройку $name") }
                .getOrNull()
            // Если Keystore недоступен, значение живёт только в памяти — открытым текстом не пишем.
            if (encrypted != null) sharedPrefs.edit { putString(name, encrypted) }
        }
        _field.value = value
    }

    private fun readStored(): String {
        val encrypted = sharedPrefs.getString(name, null)
            ?: return legacyName?.let { sharedPrefs.getString(it, null) } ?: default
        return runCatching { KeystoreCipher.decrypt(encrypted) }
            .onFailure { Timber.w(it, "Не удалось расшифровать настройку $name") }
            .getOrDefault(default)
    }

    private fun migrateLegacyPlainText() {
        val key = legacyName ?: return
        if (!sharedPrefs.contains(key)) return
        val plain = sharedPrefs.getString(key, null).orEmpty()
        if (plain.isNotEmpty() && !sharedPrefs.contains(name)) {
            setValue(plain)
            // Keystore недоступен — старую запись не трогаем, попробуем при следующем запуске.
            if (!sharedPrefs.contains(name)) return
        }
        sharedPrefs.edit { remove(key) }
    }

    private val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
        if (key == name) { _field.value = readStored() }
    }

    init {
        migrateLegacyPlainText()
        _field.value = readStored()
        sharedPrefs.registerOnSharedPreferenceChangeListener(listener)
    }

    fun clear() { sharedPrefs.unregisterOnSharedPreferenceChangeListener(listener) }
}
