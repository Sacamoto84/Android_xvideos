package com.client.xvideos.feature.room

import androidx.sqlite.db.SupportSQLiteOpenHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import java.io.File

class ExternalPathSQLiteHelperFactory(private val dbFile: File) : SupportSQLiteOpenHelper.Factory {
    override fun create(configuration: SupportSQLiteOpenHelper.Configuration): SupportSQLiteOpenHelper {
        val config = SupportSQLiteOpenHelper.Configuration.builder(configuration.context)
            .name(dbFile.absolutePath) // <-- тут полный путь, а не просто имя
            .callback(configuration.callback)
            .build()

        return FrameworkSQLiteOpenHelperFactory().create(config)
    }
}