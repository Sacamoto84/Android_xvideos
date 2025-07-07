package com.client.redgifs.db.di

import android.content.Context
import androidx.room.Room
import com.client.xvideos.AppPath
import com.client.xvideos.redgifs.db.AppRedGifsDatabase
import com.client.xvideos.redgifs.db.dao.BlockDao
import com.client.xvideos.redgifs.db.dao.GifsInfoDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import java.io.File
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RoomRedGifsModule {

    @Singleton
    @Provides
    fun provideBlockDao(appDatabase: AppRedGifsDatabase): BlockDao {
        return appDatabase.blockDao()
    }

    @Singleton
    @Provides
    fun provideGifInfoDao(appDatabase: AppRedGifsDatabase): GifsInfoDao {
        return appDatabase.gifInfoDao()
    }

    @Provides
    @Singleton
    fun provideRedGifsStockDatabase(@ApplicationContext context: Context): AppRedGifsDatabase {
        println("!!! DI RedGifs ROOM")
        val dbPath = File(AppPath.db_red, "red_database.db").apply {
            parentFile?.mkdirs()      // гарантируем, что директория есть
        }.absolutePath
        return Room.databaseBuilder(context, AppRedGifsDatabase::class.java, dbPath)
            .fallbackToDestructiveMigration()
            //.allowMainThreadQueries()
            .build()
    }

}