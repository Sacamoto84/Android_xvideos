package com.client.xvideos.di

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

// Глобальный модуль SharedPreferences
// v1.0.0 17.04.2024

@Module
@InstallIn(SingletonComponent::class)
object AppModuleSharedPreferences {

    @Provides
    @Singleton
    fun provideSharedPreferences(@ApplicationContext context: Context): SharedPreferences {
        return context.getSharedPreferences("prefs", MODE_PRIVATE)
    }

}

//
//val ed: SharedPreferences.Editor = pref!!.edit()
//	ed.putBoolean("vibro", vibroOn)
//	ed.commit()
//pref!!.getBoolean("vibro", true)
//
//class SessionCacheImpl @Inject constructor(
//    private val pref: SharedPreferences
//): SharedPreferencesImpl {
//    fun saveString(key : String, value : String){
//	    pref.edit()
//            .putString(key, value)
//            .commit()
//    }
//	fun loadBoolean(key : String, default: Boolean): Boolean{
//		return pref.getBoolean(key, default)
//	}
//	fun remove(key : String){
//		pref.edit().remove("session").apply()
//	}
//}