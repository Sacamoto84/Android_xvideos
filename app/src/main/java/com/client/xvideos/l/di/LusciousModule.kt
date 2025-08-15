package com.client.xvideos.l.di

import android.content.Context
import androidx.room.Room
import com.client.common.AppPath
import com.client.common.di.ApplicationScope
import com.client.xvideos.BuildConfig
import com.client.xvideos.l.Luscious
import com.client.xvideos.l.db.AppLDatabase
import com.redgifs.db.AppRedGifsDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import java.io.File
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LusciousModule {

    @Singleton
    @Provides
    fun provideLuscious(
        @ApplicationScope scope: CoroutineScope,
        db: AppLDatabase
    ): Luscious {
        val email = BuildConfig.luscious_email
        val password = BuildConfig.luscious_password
        val luscious = Luscious(scope, email, password, db = db)
        return luscious
    }

    @Provides
    @Singleton
    fun provideLStockDatabase(@ApplicationContext context: Context): AppLDatabase {
        println("!!! DI L ROOM")
        val dbPath = File(AppPath.db_l, "l_database.db").apply {
            parentFile?.mkdirs()      // гарантируем, что директория есть
        }.absolutePath
        return Room.databaseBuilder(context, AppLDatabase::class.java, dbPath)
            .fallbackToDestructiveMigration()
            .build()
    }

}