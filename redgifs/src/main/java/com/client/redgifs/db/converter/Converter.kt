package com.client.redgifs.db.converter

import androidx.room.TypeConverter
import com.client.redgifs.network.types.URL1
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object Converters {

    /* ---------- List<String> ---------- (как раньше) */
    private val gson = Gson()

    @TypeConverter
    fun listToJson(list: List<String>?): String? = list?.let { gson.toJson(it) }

    @TypeConverter
    fun jsonToList(json: String?): List<String>? =
        json?.let {
            val type = object : TypeToken<List<String>>() {}.type
            gson.fromJson(it, type)
        }

    /* ---------- URL1 объект ---------- */
    @TypeConverter
    fun urlToJson(url1: URL1?): String? = url1?.let { gson.toJson(it) }

    @TypeConverter
    fun jsonToUrl(json: String?): URL1? = json?.let { gson.fromJson(it, URL1::class.java) }

}