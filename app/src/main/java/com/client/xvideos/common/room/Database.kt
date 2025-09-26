package com.client.xvideos.common.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.client.xvideos.xvideos.model.ItemsX
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import java.util.Date
import javax.inject.Singleton


/**
 * ## База данных предоставляющие функции кеша для хранения строк
 *
 *  @see <img height="640"  src="https://ah-img.luscious.net/Joking42/499900/gk4rhayxqaadbfo_01JN9493Z724XCWD4KGPD740CY.1680x0.jpg"/>
 *
 *
 *
 */
@Database(
    entities = [
        ItemsX::class,
        CacheUrlStringRamEntity::class,
        CacheUrlStringRomEntity::class,
        //--- L ---
        L_AlbumPictureCacheEntity::class,
    ],
    version = 7,
    autoMigrations = [
        //AutoMigration(from = 4, to = 5)
    ],
    exportSchema = true
)
@TypeConverters(DateConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun cacheUrlStringRamDao(): CacheUrlStringRamDao
    abstract fun cacheUrlStringRomDao(): CacheUrlStringRomDao

    //--- L ---
    abstract fun albumPictureCacheDao(): L_AlbumPictureCacheDao

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
            //.allowMainThreadQueries()
            .addCallback(object : RoomDatabase.Callback() {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    // Устанавливаем размер страницы (по умолчанию 4096)
                    db.execSQL("PRAGMA page_size = 65536") // Максимальный размер
                }
            })
            .build()
    }

}

//    @Provides
//    @Singleton
//    fun provideLStockDatabase(@ApplicationContext context: Context): AppLDatabase {
//        println("!!! DI L ROOM")
//        val dbPath = File(AppPath.db_l, "l_database.db").apply {
//            parentFile?.mkdirs()      // гарантируем, что директория есть
//        }.absolutePath
//        return Room.databaseBuilder(context, AppDatabase::class.java, dbPath)
//            .fallbackToDestructiveMigration()
//            .addCallback(object : RoomDatabase.Callback() {
//                override fun onCreate(db: SupportSQLiteDatabase) {
//                    // Устанавливаем размер страницы (по умолчанию 4096)
//                    db.execSQL("PRAGMA page_size = 65536") // Максимальный размер
//                }
//            })
//            .build()
//    }