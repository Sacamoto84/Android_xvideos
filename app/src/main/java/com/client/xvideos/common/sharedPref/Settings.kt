package com.client.xvideos.common.sharedPref

import android.content.SharedPreferences
import com.client.xvideos.l.model.ThumbnailsSize

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


    /**
     * Обьем дискового кеша в МБ
     */
    val frescoDiskCacheCapacity by lazy { SettingElementInt(pref, "frescoDiskCacheCapacity", 2000) }

    /**
     * Функция автоочистки кеша при запуске приложения, очищает половину старых файлов
     */
    val frescoDiskCacheAutoClear by lazy { SettingElementBoolean(pref, "frescoDiskCacheAutoClear", false) }





    //-- xvideos ---

    //Вывод в дашбоард в две столбика
    val xvideos_row2 by lazy { SettingElementBoolean(pref, "x_row2", true) }

    val xvideos_shemale by lazy { SettingElementBoolean(pref, "x_shemale", true) }



}