package com.client.xvideos.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.client.xvideos.room.entity.FavoriteGalleryItem
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Database(entities = [FavoriteGalleryItem::class], version = 1, exportSchema = true)
abstract class AppDatabase : RoomDatabase() {
    abstract fun favoriteDao(): FavoriteGalleryDao
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
