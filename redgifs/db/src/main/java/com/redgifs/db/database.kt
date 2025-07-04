package com.redgifs.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.redgifs.db.dao.CacheMedaResponseDao
import com.redgifs.db.entity.CacheMediaResponseEntity
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import java.util.Date
import javax.inject.Singleton

@Database(
    entities = [CacheMediaResponseEntity::class],
    version = 3,
    exportSchema = true
)

@TypeConverters(DateConverter::class)
abstract class AppRedgifsDatabase : RoomDatabase() {
    abstract fun cacheMedaResponseDao(): CacheMedaResponseDao
    //abstract fun redSearchHistoryDao(): SearchRedHistoryDao
}

class DateConverter {
    @TypeConverter
    fun toDate(timestamp: Long?): Date? {
        return timestamp?.let { Date(it) }
    }

    @TypeConverter
    fun toTimestamp(date: Date?): Long? {
        return date?.time
    }
}

@Module
@InstallIn(SingletonComponent::class)
object RoomRedgigs {

    @Provides
    @Singleton
    fun provideStockDatabase(@ApplicationContext context: Context): AppRedgifsDatabase {
        println("!!! DI Redgifs ROOM")
        return Room.databaseBuilder(context, AppRedgifsDatabase::class.java, "database_redgifs")
            .fallbackToDestructiveMigration()
            .allowMainThreadQueries()
            .build()
    }


}