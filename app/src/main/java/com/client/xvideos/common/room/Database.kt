package com.client.xvideos.common.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.client.xvideos.common.room.converter.Converters
import com.client.xvideos.common.room.dao.CacheUrlStringRamDao
import com.client.xvideos.common.room.dao.CacheUrlStringRomDao
import com.client.xvideos.common.room.dao.l.L_AlbumPictureCacheDao
import com.client.xvideos.common.room.dao.r.R_BlockDao
import com.client.xvideos.common.room.dao.r.R_CacheMediaResponseDao
import com.client.xvideos.common.room.dao.r.R_GifsInfoDao
import com.client.xvideos.common.room.dao.r.R_SearchHistoryExplorerDao
import com.client.xvideos.common.room.dao.r.R_SearchHistoryNichesDao
import com.client.xvideos.common.room.entity.CacheUrlStringRamEntity
import com.client.xvideos.common.room.entity.CacheUrlStringRomEntity
import com.client.xvideos.common.room.entity.l.L_AlbumPictureCacheEntity
import com.client.xvideos.common.room.entity.r.R_BlockEntity
import com.client.xvideos.common.room.entity.r.R_CacheMediaResponseEntity
import com.client.xvideos.common.room.entity.r.R_GifsInfoEntity
import com.client.xvideos.common.room.entity.r.R_SearchHistoryExplorerEntity
import com.client.xvideos.common.room.entity.r.R_SearchHistoryNichesEntity
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

        CacheUrlStringRamEntity::class,
        CacheUrlStringRomEntity::class,
        //--- X ---
        ItemsX::class,

        //--- L ---
        L_AlbumPictureCacheEntity::class,

        //--- Red ---
        R_CacheMediaResponseEntity::class,
        R_BlockEntity::class,
        R_GifsInfoEntity::class,
        R_SearchHistoryExplorerEntity::class, //История поиска Explorer
        R_SearchHistoryNichesEntity::class    //История поиска Niches
    ],
    version = 8,
    autoMigrations = [
        //AutoMigration(from = 4, to = 5)
    ],
    exportSchema = true
)
@TypeConverters(DateConverter::class, Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun cacheUrlStringRamDao(): CacheUrlStringRamDao
    abstract fun cacheUrlStringRomDao(): CacheUrlStringRomDao

    //--- L ---
    abstract fun albumPictureCacheDao(): L_AlbumPictureCacheDao

    //--- Red ---
    abstract fun gifInfoDao(): R_GifsInfoDao
    abstract fun searchHistoryExplorerDao(): R_SearchHistoryExplorerDao

    abstract fun searchHistoryNichesDao(): R_SearchHistoryNichesDao

    abstract fun blockDao(): R_BlockDao
    abstract fun cacheMediaResponseDao(): R_CacheMediaResponseDao
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