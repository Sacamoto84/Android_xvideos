package com.client.common.preference

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PreferencesRepository(private val dataStore: DataStore<Preferences>) {
    ////////////////////////////////////////////////////////
    private val ROW2_KEY = booleanPreferencesKey("row2")
    val flowRow2: Flow<Boolean> =
        dataStore.data.map { it[ROW2_KEY] == true }
    suspend fun setRow2(enabled: Boolean) {
        dataStore.edit { it[ROW2_KEY] = enabled }
    }
    ////////////////////////////////////////////////////////
    private val SHEMALE_KEY = booleanPreferencesKey("shemale")
    val flowShemale: Flow<Boolean> = dataStore.data.map { it[SHEMALE_KEY] == true }
    suspend fun setShemale(enabled: Boolean) {
        dataStore.edit { it[SHEMALE_KEY] = enabled }
    }
    ////////////////////////////////////////////////////////


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