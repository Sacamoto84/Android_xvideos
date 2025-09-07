package com.client.xvideos.common.sharedPref

import android.content.SharedPreferences
import androidx.core.content.edit
import com.client.xvideos.l.model.ThumbnailsSize
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

class SettingElementInt(private val sharedPrefs: SharedPreferences, val name: String, val default: Int = 0) {
    private val _galleryCheckbox = MutableStateFlow(sharedPrefs.getInt(name, default))
    val field: StateFlow<Int> = _galleryCheckbox.asStateFlow()

    fun setValue(value: Int) {
        sharedPrefs.edit { putInt(name, value) }
        _galleryCheckbox.value = value
        println("!!! setValue $value _galleryCheckbox ${ _galleryCheckbox.value} ")
    }

    private val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
        if (key == name) { _galleryCheckbox.value = sharedPrefs.getInt(key, default)  }
    }

    init { sharedPrefs.registerOnSharedPreferenceChangeListener(listener) }
    fun clear() { sharedPrefs.unregisterOnSharedPreferenceChangeListener(listener) }
}


class SettingElementString(private val sharedPrefs: SharedPreferences, val name: String, val default: String = "") {
    private val _field = MutableStateFlow(sharedPrefs.getString(name, default)!!)
    val field: StateFlow<String> = _field.asStateFlow()

    fun setValue(value: String) {
        sharedPrefs.edit { putString(name, value) }
        _field.value = value
        println("!!! setValue $value _field ${ _field.value} ")
    }

    private val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
        if (key == name) { _field.value = sharedPrefs.getString(key, default)!!  }
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

    val current_count_niches by lazy { SettingElementInt(pref, "current_count_niches", 2) }

    val current_count_gifTab by lazy { SettingElementInt(pref, "current_count_gifTab", 2) }

    val current_count_likesTab by lazy { SettingElementInt(pref, "current_count_likesTab", 2) }
    val current_count_collectionTab by lazy { SettingElementInt(pref, "current_count_collectionTab", 2) }




    //-- luscious ---

    /**
     * Размер миниатюры в галерее
     */
    val thumbalistSize by lazy { SettingElementString(pref, "thumbalistSize", ThumbnailsSize.SMALL.value) }



}