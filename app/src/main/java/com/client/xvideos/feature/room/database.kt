package com.client.xvideos.feature.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.client.xvideos.feature.redgifs.db.CacheMedaResponseDao
import com.client.xvideos.feature.redgifs.db.CacheMediaResponseEntity
import com.client.xvideos.feature.room.entity.Favorites
import com.client.xvideos.feature.room.entity.Items
import com.client.xvideos.red.common.search.SearchRedHistoryDao
import com.client.xvideos.red.common.search.SearchRedHistoryEntity
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import java.util.Date
import javax.inject.Singleton

@Database(
    entities = [Items::class, Favorites::class, SearchRedHistoryEntity::class],
    version = 4,
    exportSchema = true
)
@TypeConverters(DateConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun favoriteDao(): FavoriteGalleryDao
    abstract fun itemsDao(): ItemsDao

    //abstract fun cacheMedaResponseDao(): CacheMedaResponseDao

    abstract fun redSearchHistoryDao(): SearchRedHistoryDao

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
object RoomPrefs {

    @Provides
    @Singleton
    fun provideStockDatabase(@ApplicationContext context: Context): AppDatabase {
        println("!!! DI ROOM")
        return Room.databaseBuilder(context, AppDatabase::class.java, "database")
            .fallbackToDestructiveMigration()
            .allowMainThreadQueries()
            .build()
    }


}
