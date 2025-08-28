package com.client.xvideos.l.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.client.common.AppPath
import com.client.common.di.ApplicationScope
import com.client.xvideos.BuildConfig
import com.client.xvideos.l.net.Luscious
import com.client.xvideos.l.db.AppLDatabase
import com.client.xvideos.l.repository.Repository
import com.redgifs.common.snackBar.SnackBarEvent
import com.redgifs.db.AppRedGifsDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LusciousModule {

    @Singleton
    @Provides
    fun provideRepository(
        db: AppLDatabase,
        @ApplicationScope scope: CoroutineScope,
        snackBarEvent: SnackBarEvent
    ):Repository
    {
        return Repository(db, snackBarEvent, scope, BuildConfig.luscious_email, BuildConfig.luscious_password)
    }

    @Singleton
    @Provides
    fun provideLuscious(
        @ApplicationScope scope: CoroutineScope,
        repository: Repository
    ): Luscious {
        return Luscious(scope, repository)
    }

    @Provides
    @Singleton
    fun provideLStockDatabase(@ApplicationContext context: Context): AppLDatabase {
        println("!!! DI L ROOM")
        val dbPath = File(AppPath.db_l, "l_database.db").apply {
            parentFile?.mkdirs()      // гарантируем, что директория есть
        }.absolutePath
        return Room.databaseBuilder(context, AppLDatabase::class.java, dbPath)
            .fallbackToDestructiveMigration()
            .addCallback(object : RoomDatabase.Callback() {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    // Устанавливаем размер страницы (по умолчанию 4096)
                    db.execSQL("PRAGMA page_size = 65536") // Максимальный размер
                }
            })
            .build()
    }

}