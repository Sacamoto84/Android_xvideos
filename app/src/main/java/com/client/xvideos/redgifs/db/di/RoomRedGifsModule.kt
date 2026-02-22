package com.client.xvideos.redgifs.db.di

import com.client.xvideos.common.room.AppDatabase
import com.client.xvideos.common.room.dao.r.R_BlockDao
import com.client.xvideos.common.room.dao.r.R_CacheMediaResponseDao
import com.client.xvideos.common.room.dao.r.R_GifsInfoDao
import com.client.xvideos.common.room.dao.r.R_SearchHistoryExplorerDao
import com.client.xvideos.common.room.dao.r.R_SearchHistoryNichesDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RoomRedGifsModule {

    @Singleton
    @Provides
    fun provideMediaDao(appDatabase: AppDatabase): R_CacheMediaResponseDao {
        return appDatabase.cacheMediaResponseDao()
    }

    @Singleton
    @Provides
    fun provideSearchExplorerDao(appDatabase: AppDatabase): R_SearchHistoryExplorerDao {
        return appDatabase.searchHistoryExplorerDao()
    }

    @Singleton
    @Provides
    fun provideSearchNichesDao(appDatabase: AppDatabase): R_SearchHistoryNichesDao {
        return appDatabase.searchHistoryNichesDao()
    }


    @Singleton
    @Provides
    fun provideBlockDao(appDatabase: AppDatabase): R_BlockDao {
        return appDatabase.blockDao()
    }

    @Singleton
    @Provides
    fun provideGifInfoDao(appDatabase: AppDatabase): R_GifsInfoDao {
        return appDatabase.gifInfoDao()
    }

//    @Provides
//    @Singleton
//    fun provideRedGifsStockDatabase(@ApplicationContext context: Context): AppRedGifsDatabase {
//        println("!!! DI RedGifs ROOM")
//        val dbPath = File(AppPath.db_red, "red_database.db").apply {
//            parentFile?.mkdirs()      // гарантируем, что директория есть
//        }.absolutePath
//        return Room.databaseBuilder(context, AppRedGifsDatabase::class.java, dbPath)
//            .fallbackToDestructiveMigration()
//            //.allowMainThreadQueries()
//            .build()
//    }

}