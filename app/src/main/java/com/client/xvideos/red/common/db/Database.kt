package com.client.xvideos.red.common.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.client.xvideos.AppPath
import com.client.xvideos.feature.room.ExternalPathSQLiteHelperFactory
import com.client.xvideos.feature.room.entity.Items
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import java.io.File
import java.util.Date
import javax.inject.Singleton

@Database(entities = [Items::class], version = 1, exportSchema = true)
@TypeConverters(DateConverter::class)
abstract class RedDatabase : RoomDatabase() {

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

val dbFile = File(AppPath.db_red, "red.db")

@Module
@InstallIn(SingletonComponent::class)
object RedDbModule {

    @Provides
    @Singleton
    fun provideRedDatabase(@ApplicationContext context: Context): RedDatabase {
        println("!!! DI RED ROOM")
        return Room.databaseBuilder(context, RedDatabase::class.java, "")
            .openHelperFactory(ExternalPathSQLiteHelperFactory(dbFile))
            .fallbackToDestructiveMigration()
            .allowMainThreadQueries()
            .build()
    }

}
