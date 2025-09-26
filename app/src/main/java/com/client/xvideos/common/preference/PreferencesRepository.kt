package com.client.xvideos.common.preference

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 *
 * # Переходим на  Setting TODO
 *
 *
 *
 */

class PreferencesRepository(private val dataStore: DataStore<Preferences>) {

    //////////////////////////////////
    // RedGifs
    /////////////////////////////////
    // Селектор списка сколько елементов показывать
    private val RED_PROFILE_SELECTOR_KEY = intPreferencesKey("red_profile_selector")
    val flowRedSelector: Flow<Int> = dataStore.data.map {it[RED_PROFILE_SELECTOR_KEY] ?: 1}
    suspend fun setRedSelector(value: Int) {
        dataStore.edit { it[RED_PROFILE_SELECTOR_KEY] = value }
    }
    ////////////////////////////////////////////////////////
}