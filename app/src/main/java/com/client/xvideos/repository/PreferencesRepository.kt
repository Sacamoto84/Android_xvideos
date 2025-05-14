package com.client.xvideos.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
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

}