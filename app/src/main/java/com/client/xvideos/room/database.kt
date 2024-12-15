package com.client.xvideos.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Database(entities = [  ], version = 1)
abstract class AppDatabase : RoomDatabase() {
    //abstract fun maindao(): MainDao
}

@Module
@InstallIn(SingletonComponent::class)
object AppModuleDatabase {

    @Provides
    @Singleton
    fun provideStockDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(context, AppDatabase::class.java, "ROOM_DATA_BASE_NAME")
            .fallbackToDestructiveMigration()
            .build()
    }

}