package com.client.xvideos.common.settings

import android.content.SharedPreferences
import com.client.xvideos.common.settings.element.SettingElementBoolean
import com.client.xvideos.common.settings.element.SettingElementInt
import com.client.xvideos.common.settings.element.SettingElementList
import com.client.xvideos.common.settings.element.SettingElementString
import com.client.xvideos.l.model.ThumbnailsSize
import com.google.common.reflect.TypeToken

//data class DC_galleryCount(var g0: Boolean, var g1: Boolean, var g2: Boolean, var g3: Boolean, var g4: Boolean )

object Settings {

    private lateinit var pref: SharedPreferences

    fun init(prefs: SharedPreferences) { pref = prefs }

    val current_count_niches by lazy { SettingElementInt(pref, "current_count_niches", 2) }

    val current_count_gifTab by lazy { SettingElementInt(pref, "current_count_gifTab", 2) }

    val current_count_likesTab by lazy { SettingElementInt(pref, "current_count_likesTab", 2) }
    val current_count_collectionTab by lazy { SettingElementInt( pref, "current_count_collectionTab", 2 ) }




    //-- luscious ---

    /**
     * Размер миниатюры в галерее
     */
    val thumbalistSize by lazy { SettingElementString( pref, "thumbalistSize", ThumbnailsSize.SMALL.value ) }

    /**
     * Количество столбиков в L Likes Tab T 1 2 3 4
     */
    val l_likesTab_G_0_4 by lazy {  SettingElementList<Boolean>( pref, "l_likesTab_G_0_4",  typeToken = object : TypeToken<List<Boolean>>() {}.type , default = listOf(false, true, true, true, true))  }

    /**
     * Текущее количество столбиков в L Likes Tab
     */
    val l_likesTab_column_current_count by lazy { SettingElementInt(pref, "l_likesTab_column_current_count", 2) }

    //-- xvideos ---

    //Вывод в дашбоард в две столбика
    val xvideos_row2 by lazy { SettingElementBoolean(pref, "x_row2", true) }

    val xvideos_shemale by lazy { SettingElementBoolean(pref, "x_shemale", true) }



}