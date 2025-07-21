package com.client.common.sharedPref

import android.content.Context
import android.content.SharedPreferences
import javax.inject.Inject
import javax.inject.Singleton
import androidx.core.content.edit
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import com.client.common.preference.di.dataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn


/**
 *  val gallery by Settings.galleryFlow(context).collectAsState(initial = false)
 */
class SettingElementBoolean(private val sharedPrefs: SharedPreferences, val name: String, val default: Boolean = false) {
    private val _galleryCheckbox = MutableStateFlow(sharedPrefs.getBoolean(name, default))
    val value: StateFlow<Boolean> = _galleryCheckbox.asStateFlow()

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




@Singleton
class Settings @Inject constructor(
    pref: SharedPreferences
) {

    val gallery_count = listOf(
        SettingElementBoolean(pref,"gallery_count_0", true),
        SettingElementBoolean(pref,"gallery_count_1", false),
        SettingElementBoolean(pref,"gallery_count_2", true),
        SettingElementBoolean(pref,"gallery_count_3", true),
        SettingElementBoolean(pref,"gallery_count_4", false)
    )

}