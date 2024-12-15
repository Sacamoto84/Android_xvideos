package com.client.xvideos.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.client.xvideos.room.entity.Crime
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import java.util.Date
import java.util.UUID
import javax.inject.Singleton

@Database(entities = [ Crime::class ], version = 1)
@TypeConverters(CrimeTypeConverters::class)
abstract class AppDatabase : RoomDatabase() {
    //abstract fun maindao(): MainDao
}


class CrimeTypeConverters {
    @TypeConverter
    fun fromDate(date: Date?): Long? {
        return date?.time
    }
    @TypeConverter
    fun toDate(millisSinceEpoch: Long?): Date? {
        return millisSinceEpoch?.let {
            Date(it)
        }
    }
    @TypeConverter
    fun toUUID(uuid: String?): UUID? {
        return UUID.fromString(uuid)
    }
    @TypeConverter
    fun fromUUID(uuid: UUID?): String? {
        return uuid?.toString()
    }
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