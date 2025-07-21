package com.client.common.sharedPref

import javax.inject.Inject
import android.content.SharedPreferences
import javax.inject.Singleton


@Singleton
class PrefManager @Inject constructor(
    val sharedPref :SharedPreferences
) {

    fun isKeyExist(id: String): Boolean {
        return sharedPref.all?.containsKey(id) ?: false
    }

    fun removeKey(id: String) {
        with (sharedPref.edit()) {
            remove(id)
            apply()
        }
    }

    fun setString(id: String, data: String) {
        with (sharedPref.edit()) {
            putString(id, data)
            apply()
        }
    }

    fun getString(id: String): String? {
        return sharedPref.getString(id, null)
    }

    fun setInt(id: String, data: Int) {
        with (sharedPref.edit()) {
            putInt(id, data)
            apply()
        }
    }

    fun getInt(id: String): Int {
        return sharedPref.getInt(id, -1)
    }

    fun setBoolean(id: String, data: Boolean) {
        with (sharedPref.edit()) {
            putBoolean(id, data)
            apply()
        }
    }

    fun getBoolean(id: String): Boolean {
        return sharedPref.getBoolean(id, false)
    }

    fun clear() {
        with (sharedPref.edit()) {
            clear()
            apply()
        }
    }
}
