package com.client.common.kdownloader.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseOpenHelper internal constructor(context: Context?) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            "CREATE TABLE IF NOT EXISTS " +
                    AppDbHelper.TABLE_NAME.toString() + "( " +
                    DownloadModel.Companion.ID + " INTEGER PRIMARY KEY, " +
                    DownloadModel.Companion.URL + " VARCHAR, " +
                    DownloadModel.Companion.ETAG + " VARCHAR, " +
                    DownloadModel.Companion.DIR_PATH + " VARCHAR, " +
                    DownloadModel.Companion.FILE_NAME + " VARCHAR, " +
                    DownloadModel.Companion.TOTAL_BYTES + " INTEGER, " +
                    DownloadModel.Companion.DOWNLOADED_BYTES + " INTEGER, " +
                    DownloadModel.Companion.LAST_MODIFIED_AT + " INTEGER " +
                    ")"
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, i: Int, i1: Int) {}

    companion object {
        private const val DATABASE_NAME = "kdownloader.db"
        private const val DATABASE_VERSION = 1
    }
}