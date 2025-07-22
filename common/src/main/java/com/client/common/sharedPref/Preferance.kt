package com.client.common.sharedPref

import android.content.SharedPreferences
import androidx.core.content.edit
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SettingElementBoolean(private val sharedPrefs: SharedPreferences, val name: String, val default: Boolean = false) {
    private val _galleryCheckbox = MutableStateFlow(sharedPrefs.getBoolean(name, default))
    val field: StateFlow<Boolean> = _galleryCheckbox.asStateFlow()

    fun setValue(value: Boolean) {
        sharedPrefs.edit { putBoolean(name, value) }
        _galleryCheckbox.value = value
    }
    private val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
        if (key == name) { _galleryCheckbox.value = sharedPrefs.getBoolean(key, default)  }
    }
    init { sharedPrefs.registerOnSharedPreferenceChangeListener(listener) }
    fun clear() { sharedPrefs.unregisterOnSharedPreferenceChangeListener(listener) }
}

object Settings {

    private lateinit var pref: SharedPreferences

    fun init(prefs: SharedPreferences) {
        pref = prefs
    }

    val gallery_count: List<SettingElementBoolean> by lazy {
        listOf(
            SettingElementBoolean(pref, "gallery_count_0", true),
            SettingElementBoolean(pref, "gallery_count_1", false),
            SettingElementBoolean(pref, "gallery_count_2", true),
            SettingElementBoolean(pref, "gallery_count_3", true),
            SettingElementBoolean(pref, "gallery_count_4", false),
        )
    }
}