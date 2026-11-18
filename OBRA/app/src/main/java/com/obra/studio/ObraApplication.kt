package com.obra.studio

import android.app.Application
import android.util.Log
import com.obra.studio.data.local.ObraDatabaseHelper

/** Owns the app-process database connection without coupling it to a screen. */
class ObraApplication : Application() {
    private var databaseHelper: ObraDatabaseHelper? = null

    override fun onCreate() {
        super.onCreate()

        val databaseFile = getDatabasePath(ObraDatabaseHelper.DATABASE_NAME)
        Log.i(
            TAG,
            "SQLite initialization attempted: file=${ObraDatabaseHelper.DATABASE_NAME}, " +
                "version=${ObraDatabaseHelper.DATABASE_VERSION}, path=${databaseFile.absolutePath}"
        )

        val helper = ObraDatabaseHelper(this)
        databaseHelper = helper
        try {
            val database = helper.writableDatabase
            val tables = database.rawQuery(
                """SELECT name FROM sqlite_master
                    WHERE type = 'table'
                      AND name NOT LIKE 'sqlite_%'
                      AND name != 'android_metadata'
                    ORDER BY name""".trimIndent(),
                null
            ).use { cursor ->
                buildList {
                    while (cursor.moveToNext()) add(cursor.getString(0))
                }.joinToString()
            }
            val foreignKeysEnabled = database.rawQuery("PRAGMA foreign_keys", null).use { cursor ->
                cursor.moveToFirst() && cursor.getInt(0) == 1
            }
            Log.i(
                TAG,
                "SQLite initialization succeeded: file=${ObraDatabaseHelper.DATABASE_NAME}, " +
                    "version=${ObraDatabaseHelper.DATABASE_VERSION}, path=${databaseFile.absolutePath}, " +
                    "size=${databaseFile.length()} bytes, foreignKeysEnabled=$foreignKeysEnabled, " +
                    "tables=[$tables]"
            )
        } catch (error: Exception) {
            helper.close()
            databaseHelper = null
            Log.e(
                TAG,
                "SQLite initialization failed: file=${ObraDatabaseHelper.DATABASE_NAME}, " +
                    "version=${ObraDatabaseHelper.DATABASE_VERSION}, path=${databaseFile.absolutePath}",
                error
            )
            throw error
        }
    }

    override fun onTerminate() {
        databaseHelper?.close()
        databaseHelper = null
        super.onTerminate()
    }

    companion object {
        private const val TAG = "ObraDatabase"
    }
}
