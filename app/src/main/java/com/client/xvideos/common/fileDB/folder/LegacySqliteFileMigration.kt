package com.client.xvideos.common.fileDB.folder

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.client.xvideos.common.AppPath
import com.client.xvideos.r.model.GifsInfo
import com.client.xvideos.r.model.URL1
import com.client.xvideos.r.model.sanitizeOrNull
import com.google.gson.Gson
import com.redgifs.common.block.useCase.blockItem as writeBlockedGif
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.File

object LegacySqliteFileMigration {
    private const val LEGACY_DATABASE_NAME = "database"
    private const val MIGRATION_MARKER = ".legacy_sqlite_migrated"

    suspend fun migrateIfNeeded(context: Context, db: AppFileDatabase) = withContext(Dispatchers.IO) {
        val marker = File(AppPath.file_db, MIGRATION_MARKER)
        if (marker.exists()) return@withContext

        val legacyDbFile = context.getDatabasePath(LEGACY_DATABASE_NAME)
        if (!legacyDbFile.exists()) {
            marker.parentFile?.mkdirs()
            marker.writeText(currentFileDbTimeText(), Charsets.UTF_8)
            return@withContext
        }

        runCatching {
            SQLiteDatabase.openDatabase(legacyDbFile.absolutePath, null, SQLiteDatabase.OPEN_READONLY).use { sql ->
                migrateStringCache(sql, "cache_url_string_rom", db.cacheUrlStringRom)
                migrateStringCache(sql, "cache_media_response", db.rCacheMediaResponse)
                migrateStringCache(sql, "l_album_picture_cache", db.lAlbumPictureCache, keyColumn = "id")
                migrateSearchHistory(sql, "r_search_history_explorer", db.rSearchHistoryExplorerTable)
                migrateSearchHistory(sql, "r_search_history_niches", db.rSearchHistoryNichesTable)
                migrateBlockedGifs(sql)
            }
        }.onFailure {
            Timber.e(it, "Legacy SQLite migration failed")
        }

        marker.parentFile?.mkdirs()
        marker.writeText(currentFileDbTimeText(), Charsets.UTF_8)
    }

    private suspend fun migrateStringCache(
        sql: SQLiteDatabase,
        tableName: String,
        table: FileStringCacheTable,
        keyColumn: String = "url"
    ) {
        if (!sql.hasTable(tableName)) return

        sql.rawQuery("SELECT * FROM $tableName", null).use { cursor ->
            while (cursor.moveToNext()) {
                val key = cursor.string(keyColumn) ?: continue
                val content = cursor.string("content") ?: continue
                table.insert(
                    FileStringCacheEntry(
                        key = key,
                        content = content,
                        timeCreate = cursor.long("timeCreate", System.currentTimeMillis()),
                        timeCreateText = cursor.string("timeCreateText") ?: currentFileDbTimeText()
                    )
                )
            }
        }
    }

    private suspend fun migrateSearchHistory(
        sql: SQLiteDatabase,
        tableName: String,
        table: FolderTable
    ) {
        if (!sql.hasTable(tableName)) return

        sql.rawQuery("SELECT * FROM $tableName", null).use { cursor ->
            while (cursor.moveToNext()) {
                val text = cursor.string("text") ?: continue
                val timeCreate = cursor.long("timeCreate", System.currentTimeMillis())
                table.upsert(
                    key = text,
                    fields = mapOf(
                        "text" to text,
                        FolderTable.FIELD_TIME_CREATE to timeCreate.toString(),
                        FolderTable.FIELD_TIME_CREATE_TEXT to currentFileDbTimeText()
                    )
                )
            }
        }
    }

    private fun migrateBlockedGifs(sql: SQLiteDatabase) {
        if (!sql.hasTable("block") || !sql.hasTable("gifs_info")) return

        val blockedIds = mutableSetOf<String>()
        sql.rawQuery("SELECT gifId FROM block WHERE gifId IS NOT NULL", null).use { cursor ->
            while (cursor.moveToNext()) {
                cursor.string("gifId")?.let(blockedIds::add)
            }
        }
        if (blockedIds.isEmpty()) return

        val gson = Gson()
        sql.rawQuery("SELECT * FROM gifs_info", null).use { cursor ->
            while (cursor.moveToNext()) {
                val id = cursor.string("id") ?: continue
                if (id !in blockedIds) continue

                val item = GifsInfo(
                    id = id,
                    createDate = cursor.long("createDate", 0L),
                    likes = cursor.int("likes", 0),
                    width = cursor.int("width", 100),
                    height = cursor.int("height", 100),
                    tags = parseStringList(gson, cursor.string("tags")),
                    description = cursor.string("description").orEmpty(),
                    views = cursor.longOrNull("views"),
                    type = cursor.int("type", 0),
                    userName = cursor.string("userName").orEmpty(),
                    urls = cursor.string("urls")?.let { gson.fromJson(it, URL1::class.java) } ?: URL1(),
                    duration = cursor.doubleOrNull("duration"),
                    hls = cursor.booleanOrNull("hls"),
                    niches = cursor.string("niches")?.let { parseStringList(gson, it) }
                ).sanitizeOrNull() ?: continue

                writeBlockedGif(item)
            }
        }
    }

    private fun parseStringList(gson: Gson, json: String?): List<String> {
        if (json.isNullOrBlank()) return emptyList()
        return runCatching {
            gson.fromJson(json, Array<String>::class.java)?.toList().orEmpty()
        }.getOrDefault(emptyList())
    }

    private fun SQLiteDatabase.hasTable(tableName: String): Boolean {
        rawQuery(
            "SELECT name FROM sqlite_master WHERE type='table' AND name=? LIMIT 1",
            arrayOf(tableName)
        ).use { cursor ->
            return cursor.moveToFirst()
        }
    }

    private fun Cursor.string(column: String): String? {
        val index = getColumnIndex(column)
        if (index < 0 || isNull(index)) return null
        return getString(index)
    }

    private fun Cursor.long(column: String, default: Long): Long {
        val index = getColumnIndex(column)
        if (index < 0 || isNull(index)) return default
        return getLong(index)
    }

    private fun Cursor.longOrNull(column: String): Long? {
        val index = getColumnIndex(column)
        if (index < 0 || isNull(index)) return null
        return getLong(index)
    }

    private fun Cursor.int(column: String, default: Int): Int {
        val index = getColumnIndex(column)
        if (index < 0 || isNull(index)) return default
        return getInt(index)
    }

    private fun Cursor.doubleOrNull(column: String): Double? {
        val index = getColumnIndex(column)
        if (index < 0 || isNull(index)) return null
        return getDouble(index)
    }

    private fun Cursor.booleanOrNull(column: String): Boolean? {
        val index = getColumnIndex(column)
        if (index < 0 || isNull(index)) return null
        return getInt(index) != 0
    }
}
