package com.client.redgifs.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.client.xvideos.feature.room.DateConverter
import com.client.xvideos.redgifs.db.entity.CacheMedaResponseDao
import com.client.xvideos.redgifs.db.entity.CacheMediaResponseEntity
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Database(
    entities = [CacheMediaResponseEntity::class],
    version = 1,
    exportSchema = true
)
@TypeConverters(DateConverter::class)
abstract class AppRedGifsDatabase : RoomDatabase() {
    abstract fun cacheMedaResponseDao(): CacheMedaResponseDao
}

@Module
@InstallIn(SingletonComponent::class)
object RoomRedgifsPrefs {

    @Provides
    @Singleton
    fun provideRedGifsStockDatabase(@ApplicationContext context: Context): AppRedGifsDatabase {
        println("!!! DI ROOM")
        return Room.databaseBuilder(context, AppRedGifsDatabase::class.java, "red_database")
            .fallbackToDestructiveMigration()
            .allowMainThreadQueries()
            .build()
    }

}